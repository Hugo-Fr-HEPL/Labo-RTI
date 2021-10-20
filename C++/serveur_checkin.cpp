#include "serveur_checkin.h"

// Check lug.csv to not write the same ticket twice
// Caractères de fin de string dnas les messages client-server

//SQL
// Poids bagage
// Bagage payé?
// Date du vol
// IdClient dans billet - pas l'inverse

int main() {
	int hSocketEcoute, hSocketService;
	struct sockaddr_in adresse;
	properties prop = sock.Load_Properties(PROPFILE);
    pthread_t threadHandle[prop.nbServer];
    int hSocketConnectee[prop.nbServer];
    paramThread param;


// Initialisation des Threads
    pthread_mutex_init(&mutexIndiceCourant, NULL);
    pthread_cond_init(&condIndiceCourant, NULL);
    pthread_mutex_init(&mutexFile, NULL);

	hSocketEcoute = sock.Create_Socket(AF_INET, SOCK_STREAM, 0);

	adresse = sock.Infos_Host(prop);

    if(sock.Bind_Socket(hSocketEcoute, (struct sockaddr*)&adresse) != EXIT_SUCCESS) return EXIT_FAILURE;
//while(sock.Bind_Socket(hSocketEcoute, (struct sockaddr*)&adresse) != 0) adresse.sin_port += 1;

// Pool de Threads pour le serveur
    for(int i = 0; i < prop.nbServer; i++) {
        param.i = i;
        hSocketConnectee[i] = -1;
        param.hSocketConnectee = hSocketConnectee;
        pthread_create(&threadHandle[i], NULL, fctThread, (void*)&param);
        printf("Creation du thread %d\n", i);
        pthread_detach(threadHandle[i]);
    }


    do {
        sock.Listen_Server(hSocketEcoute);

        hSocketService = sock.Accept_Server(hSocketEcoute, (struct sockaddr*)&adresse);

        printf("Recherche d'un socket connectee libre ...\n");
        int i;
        for(i = 0; i < prop.nbServer && hSocketConnectee[i] != -1; i++);

        if(i == prop.nbServer) {
            printf("Plus de connexion disponible\n");
            char msgServeur[MAXSTRING] = NO_CO;
            sock.Send_Message(hSocketService, msgServeur, 0);

            close(hSocketService);
        } else {
            printf("Connexion sur la socket num %d\n", i);

            char msgServeur[MAXSTRING] = CO;
            sock.Send_Message(hSocketService, msgServeur, 0);

            pthread_mutex_lock(&mutexIndiceCourant);

            hSocketConnectee[i] = hSocketService;
            indiceCourant = i;

            pthread_mutex_unlock(&mutexIndiceCourant);
            pthread_cond_signal(&condIndiceCourant);
        }
    } while(1);

    close(hSocketEcoute);
    printf("Socket Ecoute ferme\n");
    close(hSocketService);
    printf("Socket Ecoute ferme\n");

	return EXIT_SUCCESS;
}

/* Thread pour permettre à plusieurs clients de se connecter */
void* fctThread(void* param) {
    int vr = ((paramThread*)param)->i;
    int* hSocketConnectee = ((paramThread*)param)->hSocketConnectee;	

    char msgClient[MAXSTRING]; //, msgServeur[MAXSTRING];
    int finDialogue=0, iCliTraite;
    int hSocketServ;
	char* buf = (char*)malloc(30);
	char* bag;

    while(1) {
//Attente d'un client à traiter
        pthread_mutex_lock(&mutexIndiceCourant);

        while(indiceCourant == -1)
            pthread_cond_wait(&condIndiceCourant, &mutexIndiceCourant);

        iCliTraite = indiceCourant;
        indiceCourant = -1;
        hSocketServ = *(hSocketConnectee+iCliTraite);

        pthread_mutex_unlock(&mutexIndiceCourant);
        sprintf(buf, "Je m'occupe du numero %d ...", iCliTraite);


// Dialogue Thread-Client
        finDialogue = 0;
        do {
            if(sock.Receive_Message(hSocketServ, msgClient, 0) == EXIT_FAILURE) {
                break;
            }
            cout << "Message recu " << msgClient << endl;

            switch(atoi(msgClient)) {
            case LOGIN_OFFICER:
                while(1) {
                    if(VerifLogin(hSocketServ) == EXIT_SUCCESS)
                        break;
                    else if(VerifLogin(hSocketServ) == 2) {
                        finDialogue = 1;
                        break;
                    }
                }
                break;
            case LOGOUT_OFFICER: // TO DO
                break;
            case CHECK_TICKET:
                while(1) {
                    if(VerifTicket(hSocketServ) == EXIT_SUCCESS)
                        break;
                    else if(VerifTicket(hSocketServ) == 2) {
                        finDialogue = 1;
                        break;
                    }
                }
                break;
            case CHECK_LUGGAGE:
                bag = VerifLuggage(hSocketServ);
                if(strcmp(bag, "EOC") == 0) {
                    finDialogue = 1;
                    break;
                }
                break;
            case PAYMENT_DONE:
                if(PaymentDone(hSocketServ, bag) == 2) {
                    finDialogue = 1;
                    break;
                }
                break;
            }
        } while(!finDialogue);
        pthread_mutex_lock(&mutexIndiceCourant);
        hSocketConnectee[iCliTraite] = -1;
        pthread_mutex_unlock(&mutexIndiceCourant);
    }
    close(hSocketServ);
    return (void*)vr;
}


/*
    Reçoit les infos du client et vérifie si elles sont valides pour se connecter
*/
int VerifLogin(int hSocketServ) {
    char msgClient[MAXSTRING], msgServeur[MAXSTRING];
    char *pt = NULL;


// Réception du message
	if(sock.Receive_Message(hSocketServ, msgClient, 0) == EXIT_FAILURE)
        return 2;
    char* word = (char*)malloc(sizeof(msgClient));
    strcpy(word, msgClient);

    char* login = strtok_r(word, ";", &pt);
    word = NULL;
    char* password = strtok_r(word, "\0", &pt);


// Vérification des infos
    pthread_mutex_lock(&mutexFile);

	FILE* fp;
	fp = fopen(LOGFILE, "r+t");
	if(fp != NULL) {
		fseek(fp, 0, SEEK_END);
		int size = ftell(fp);
		rewind(fp);

		char txt[200];
		fread(txt, size, 1, fp);

		char *log = NULL, *pwd = NULL;
		for(int i = 1; ; i++) {
			if((word = sock.Read_Line(i, txt)) == NULL) {
                sprintf(msgServeur, "%d", LOG);
                break;
			} else {
				log = strtok_r(word, ";", &pt);

				if(strcmp(log, login) == 0) {
					word = NULL;
					pwd = strtok_r(word, "\0", &pt);

                    if(strcmp(pwd, password) == 0) {
                        sprintf(msgServeur, "%d", OK);
                        sock.Send_Message(hSocketServ, msgServeur, 0);
                        cout << "Message envoye " << msgServeur << endl;
                        free(word);
                        pthread_mutex_unlock(&mutexFile);
                        return EXIT_SUCCESS;
                    } else {
                        sprintf(msgServeur, "%d", PWD);
                        break;
                    }
				}
			}
		}
		fclose(fp);
	} else
        sprintf(msgServeur, "%d", EMPTY);
    pthread_mutex_unlock(&mutexFile);

    sock.Send_Message(hSocketServ, msgServeur, 0);
    cout << "Message envoye " << msgServeur << endl;

    free(word);

	return EXIT_FAILURE;
}


/*
    Reçoit un billet et vérifie s'il est dans la base de données
*/
int VerifTicket(int hSocketServ) {
    char msgClient[MAXSTRING], msgServeur[MAXSTRING];


/* Réception du message */
	if(sock.Receive_Message(hSocketServ, msgClient, 0) == EXIT_FAILURE)
        return 2;
	char* bi = (char*)malloc(sizeof(msgClient));
	strcpy(bi, msgClient);


/* Vérifie si les billets sont dans le fichiers .csv */
    pthread_mutex_lock(&mutexFile);

	FILE* fp;
	fp = fopen(TICFILE, "r+t");
	if(fp != NULL) {
		fseek(fp, 0, SEEK_END);
		int size = ftell(fp);
		rewind(fp);

		char txt[200];
		fread(txt, size, 1, fp);

        char* word = NULL;
		for(int i = 1; ; i++) {
			if((word = sock.Read_Line(i, txt)) == NULL) {
                sprintf(msgServeur, "%d", LOG);
                break;
			} else {
				if(strcmp(bi, word) == 0) {
                    sprintf(msgServeur, "%d", OK);
                    sock.Send_Message(hSocketServ, msgServeur, 0);
                    cout << "Message envoye " << msgServeur << endl;
                    free(bi);
                    pthread_mutex_unlock(&mutexFile);
                    return EXIT_SUCCESS;
                } else {
                    sprintf(msgServeur, "%d", TIC);
                    break;
                }
			}
		}
		fclose(fp);
	} else
        sprintf(msgServeur, "%d", EMPTY);
    pthread_mutex_unlock(&mutexFile);

    sock.Send_Message(hSocketServ, msgServeur, 0);
    cout << "Message envoye " << msgServeur << endl;

    free(bi);

    return EXIT_FAILURE;
}


/*
    Calcul l'excès de poids et montre le prix
*/
char* VerifLuggage(int hSocketServ) {
    char msgClient[MAXSTRING], msgServeur[MAXSTRING];
    char *pt = NULL, *poidc = NULL;
	int exces = 0, supp = 0, total = 0, poidi, i=1;


// Réception du message
	if(sock.Receive_Message(hSocketServ, msgClient, 0) == EXIT_FAILURE) {
        char* ret = (char*)malloc(sizeof("EOC"));
        strcpy(ret, "EOC");
        return ret;
    }
    char* word = (char*)malloc(sizeof(msgClient));
	char* ret = (char*)malloc(sizeof(msgClient));
    strcpy(word, msgClient);
	strcpy(ret, msgClient);


// Récuération des poids
	while(true) {
		poidc = strtok_r(word, ";", &pt);
		word = NULL;
		
		if(i % 2 != 0) { // Ne pas prendre les N et O
			if(poidc == NULL) break;

			poidi = atoi(poidc);
			total += poidi;
			if(poidi > 20) {
				exces += poidi - 20;
				supp += (poidi- 20) * 3;
			}
		}
		i++;
	}
	cout << "Poids total bagages : " << total << "kg" << endl;
	cout << "Excedent poids : " << exces << "kg" << endl;
	cout << "Supplement a payer : " << supp << " EUR" << endl;

	sprintf(msgServeur, "%d", supp);
	sock.Send_Message(hSocketServ, msgServeur, 0);

    free(word);

	return ret;
}


/*
    Vérifie si le paiement a été fait
    Encode les bagages dans un fichier
*/
int PaymentDone(int hSocketServ, char* bag) {
    char msgClient[MAXSTRING];//, msgServeur[100];


// Réception du message
	if(sock.Receive_Message(hSocketServ, msgClient, 0) == EXIT_FAILURE)
        return 2;
    if(strcmp(msgClient, "N") == 0) {
        sock.Send_Message(hSocketServ, EOC, 0);
        cout << "Fin de connexion" << endl;
        return 2;
    }
	cout << "Paiement effectue ? Y" << endl;


// Séparation du code billet
    char *pt = NULL, *ticket = msgClient;
    char *tic1 = strtok_r(ticket, "-", &pt);
    ticket = NULL;

    char *tic2 = strtok_r(ticket, "-", &pt);
    ticket = NULL;

    char *tic3 = strtok_r(ticket, "\0", &pt);
    ticket = NULL;

// Sauvegarde du fichier
    pthread_mutex_lock(&mutexFile);

    FILE* fp;
    fp = fopen(LUGFILE, "a+t");
    if(fp != NULL) {
        int i = 1, j = 1;
        char line[50] = "", nb[3] = "";
        char* valise = NULL;
        pt = NULL;

        while(true) {
            valise = strtok_r(bag, ";", &pt);
            bag = NULL;

            if(valise == NULL) break;

            if(i % 2 == 0 && i > 0) { // Pour prendre les N et O
                strcpy(line, "\r");
                strcat(line, tic1);
                strcat(line, "-");
                strcat(line, "NAMETEMP");
                strcat(line, "-");
                strcat(line, tic2);
                strcat(line, "-");
                strcat(line, tic3);
                strcat(line, "-");

                sprintf(nb, "%d", j);

                if(j < 100) strcat(line, "0");
                if(j < 10) strcat(line, "0");
                strcat(line, nb);

                if(strcmp(valise, "O") == 0) {
                    strcat(line, ";VALISE");
                } else {
                    strcat(line, ";PASVALISE");
                }
                j++;

                fprintf(fp, line, sizeof(line));
            }
            i++;
        }
        fclose(fp);
    }
    pthread_mutex_unlock(&mutexFile);

    sprintf(msgClient, "%d", OK);
	sock.Send_Message(hSocketServ, msgClient, 0);

	return EXIT_SUCCESS;
}
