#include "serveur_checkin.h"


int main() {
	int hSocketEcoute, hSocketService;
	struct sockaddr_in adresse;
	properties prop = Load_Properties(FILENAME);
    pthread_t threadHandle[prop.nbServer];
    int hSocketConnectee[prop.nbServer];
    paramThread param;


// Initialisation des Threads
    pthread_mutex_init(&mutexIndiceCourant, NULL);
    pthread_cond_init(&condIndiceCourant, NULL);

	hSocketEcoute = Create_Socket(AF_INET, SOCK_STREAM, 0);

	adresse = Infos_Host(prop);

	Bind_Socket(hSocketEcoute, (struct sockaddr*)&adresse, sizeof(struct sockaddr_in));

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
        Listen_Server(hSocketEcoute, SOMAXCONN);

        hSocketService = Accept_Server(hSocketEcoute, (struct sockaddr*)&adresse, sizeof(struct sockaddr_in));

        printf("Recherche d'un socket connectee libre ...\n");
        int i;
        for(i = 0; i < prop.nbServer && hSocketConnectee[i] != -1; i++);

        if(i == prop.nbServer) {
            printf("Plus de connexion disponible\n");

            char* msgServeur = NULL;
            sprintf(msgServeur, DOC);
            Send_Message(hSocketService, msgServeur, MAXSTRING, 0);
        } else {
            printf("Connexion sur la socket num %d\n", i);

            pthread_mutex_lock(&mutexIndiceCourant);

            hSocketConnectee[i] = hSocketService;
            indiceCourant = i;

            pthread_mutex_unlock(&mutexIndiceCourant);
            pthread_cond_signal(&condIndiceCourant);
        }

        //Receive_Message(hSocketService, msgClient, MAXSTRING, 0);
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
            if(Receive_Message(hSocketServ, msgClient, MAXSTRING, 0) == EXIT_FAILURE) {
                finDialogue = 1;
                break;
            }
            cout << "Message recu " << msgClient << endl;

            if(strcmp(msgClient, EOC) == 0) {
                finDialogue = 1;
                Send_Message(hSocketServ, EOC, MAXSTRING, 0);
                break;
            } else {
                switch(atoi(msgClient)) {
                case LOGIN_OFFICER:
                    while(VerifLogin(hSocketServ) != EXIT_SUCCESS);
                    break;
                case LOGOUT_OFFICER: // TO DO
                    break;
                case CHECK_TICKET:
                    while(VerifTicket(hSocketServ) != EXIT_SUCCESS);
                    break;
                case CHECK_LUGGAGE: 
					bag = VerifLuggage(hSocketServ);
                    break;
                case PAYMENT_DONE:
                    PaymentDone(hSocketServ, bag);
                    break;
                }
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
bool VerifLogin(int hSocketServ) {
    char msgClient[100], msgServeur[100];
    char *pt = NULL;


// Réception du message
	Receive_Message(hSocketServ, msgClient, MAXSTRING, 0);
    char* word = (char*)malloc(sizeof(msgClient));
    strcpy(word, msgClient);

    char* login = strtok_r(word, ";", &pt);
    word = NULL;
    char* password = strtok_r(word, "\0", &pt);


// Vérification des infos
	FILE* fp;
	fp = fopen("login.csv", "r+t");
	if(fp != NULL) {
		fseek(fp, 0, SEEK_END);
		int size = ftell(fp);
		rewind(fp);

		char txt[200];
		fread(txt, size, 1, fp);

		char *log = NULL, *pwd = NULL;
		for(int i = 1; ; i++) {
			if((word = Read_Line(i, txt)) == NULL) {
                strcpy(msgServeur, LOG);
                break;
			} else {
				log = strtok_r(word, ";", &pt);

				if(strcmp(log, login) == 0) {
					word = NULL;
					pwd = strtok_r(word, "\0", &pt);

                    if(strcmp(pwd, password) == 0) {
                        Send_Message(hSocketServ, OK, MAXSTRING, 0);
                        cout << "Message envoye " << OK << endl;
                        free(word);// free(pt); free(log); free(pwd);
                        return EXIT_SUCCESS;
                    } else {
                        strcpy(msgServeur, PWD);
                        break;
                    }
				}
			}
		}
		fclose(fp);
	} else
        strcpy(msgServeur, EMPTY);  // Pas de fichier

    Send_Message(hSocketServ, msgServeur, MAXSTRING, 0);
    cout << "Message envoye " << msgServeur << endl;

    free(word);

	return EXIT_FAILURE;
}


/*
    Reçoit un billet et vérifie s'il est dans la base de données
*/
bool VerifTicket(int hSocketServ) {
    char msgClient[100], msgServeur[100];


/* Réception du message */
	Receive_Message(hSocketServ, msgClient, MAXSTRING, 0);
	char* bi = (char*)malloc(sizeof(msgClient));
	strcpy(bi, msgClient);


/* Vérifie si les billets sont dans le fichiers .csv */
	FILE* fp;
	fp = fopen("billets.csv", "r+t");
	if(fp != NULL) {
		fseek(fp, 0, SEEK_END);
		int size = ftell(fp);
		rewind(fp);

		char txt[200];
		fread(txt, size, 1, fp);

        char* word = NULL;
		for(int i = 1; ; i++) {
			if((word = Read_Line(i, txt)) == NULL) {
                strcpy(msgServeur, LOG);
                break;
			} else {
				if(strcmp(bi, word) == 0) {
                    Send_Message(hSocketServ, OK, MAXSTRING, 0);
                    cout << "Message envoye " << OK << endl;
                    free(bi);
                    return EXIT_SUCCESS;
                } else {
                    strcpy(msgServeur, TIC);
                    break;
                }
			}
		}
		fclose(fp);
	} else
        strcpy(msgServeur, EMPTY);  // Pas de fichier

    Send_Message(hSocketServ, msgServeur, MAXSTRING, 0);
    cout << "Message envoye " << msgServeur << endl;

    free(bi);

    return EXIT_FAILURE;
}


/*
    Calcul l'excès de poids et montre le prix
*/
char* VerifLuggage(int hSocketServ) {
    char msgClient[100], msgServeur[100];
    char *pt = NULL, *poidc = NULL;
	int exces = 0, supp = 0, total = 0, poidi, i=1;


// Réception du message
	Receive_Message(hSocketServ, msgClient, MAXSTRING, 0);
    char* word = (char*)malloc(sizeof(msgClient));
	char* ret = (char*)malloc(sizeof(msgClient));
    strcpy(word, msgClient);
	strcpy(ret, msgClient);


// Récuération des poids
	while(true) {
		poidc = strtok_r(word, ";", &pt);
		word = NULL;
		
		if(i % 2 != 0) { //pour ne pas prendre les N et O
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
	Send_Message(hSocketServ, msgServeur, MAXSTRING, 0);

    free(word);

	return ret;
}


/*
    Vérifie si le paiement a été fait
    Encode les bagages dans un fichier
*/
bool PaymentDone(int hSocketServ, char* bag) {
    char msgClient[100];//, msgServeur[100];


// Réception du message
	Receive_Message(hSocketServ, msgClient, MAXSTRING, 0);
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

            if(i % 2 == 0 && i > 0) { //pour prendre les N et O
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
	Send_Message(hSocketServ, OK, MAXSTRING, 0);

	return EXIT_SUCCESS;
}
