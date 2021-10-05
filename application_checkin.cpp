#include "reseaux.h"


void CloseConnection(int socket);
bool Login(int socket);
bool Ticket(int socket);
bool Luggage(int socket);

int main() {
	int hSocketClient;
	struct sockaddr_in adresse;
	properties prop = Load_Properties(FILENAME);
	struct in_addr adresseIP;
	char* send = (char*)malloc(sizeof(int));

	char msgClient[MAXSTRING] = "bonjour petite peruche";
	char msgServeur[MAXSTRING];



	hSocketClient = Create_Socket(AF_INET, SOCK_STREAM, 0);

	adresse = Infos_Host(prop);
	adresseIP = adresse.sin_addr;

	Connect_Client(hSocketClient, (struct sockaddr*)&adresse, sizeof(struct sockaddr_in));


// Connexion
	sprintf(send, "%d", LOGIN_OFFICER);
	Send_Message(hSocketClient, send, MAXSTRING, 0);
	while(Login(hSocketClient) != EXIT_SUCCESS);


	do {
// Encoder ticket
		sprintf(send, "%d", CHECK_TICKET);
		Send_Message(hSocketClient, send, MAXSTRING, 0);
		while(Ticket(hSocketClient) != EXIT_SUCCESS);

//Encoder bagages
		sprintf(send, "%d", CHECK_LUGGAGE);
		//Send_Message(hSocketClient, send, MAXSTRING, 0);
		//while(Luggage(hSocketClient) != EXIT_SUCCESS);
	} while(1);

	do {
		Send_Message(hSocketClient, msgClient, MAXSTRING, 0);

		if(strcmp(msgClient, EOC)) {
			Receive_Message(hSocketClient, msgServeur, MAXSTRING, 0);

			strcpy(msgClient, EOC);
		}
	} while(strcmp(msgClient, EOC) && strcmp(msgServeur, DOC));

	CloseConnection(hSocketClient);
}

/* Ferme tout proprement */
void CloseConnection(int hSocketClient) {
	close(hSocketClient);
    printf("Socket client ferme\n");
	exit(EXIT_FAILURE);
}


/*
	Envoie ses infos de connexion au serveur
	return EXIT_SUCCESS on success
*/
bool Login(int hSocketClient) {
	char login[30], password[30];
	char msgClient[60] = {"\0"}, msgServeur[100];


	cout << "Encodez votre login : ";
	cin >> login;
	cout << "Encodez votre mot de passe : ";
	cin >> password;

	strcat(msgClient, login);
	strcat(msgClient, ";");
	strcat(msgClient, password);
	strcat(msgClient, "\0");

	Send_Message(hSocketClient, msgClient, MAXSTRING, 0);
	Receive_Message(hSocketClient, msgServeur, MAXSTRING, 0);


	if(!strcmp(msgServeur, LOG)) {
		cout << "Le login n'existe pas\n" << endl;
		return EXIT_FAILURE;
	} else if(!strcmp(msgServeur, PWD)) {
		cout << "Le mot de passe est incorrect\n" << endl;
		return EXIT_FAILURE;
	} else if(!strcmp(msgServeur, EMPTY)) {
		cout << "Il n'existe aucun compte actuellement ...\n" << endl;
		return EXIT_FAILURE;
	} else {
		cout << "Connexion reussie !\n" << endl;
		return EXIT_SUCCESS;
	}
	return EXIT_FAILURE;
}


/*
	Envoie les billets au serveur
	return EXIT_SUCCESS on success
*/
bool Ticket(int hSocketClient) {
	int nbAccomp;
	char billet[20], txt[20];
	char msgClient[60] = {"\0"}, msgServeur[100];


	cout << "Numero de billet ? ";
	cin >> billet;
	cout << endl << "Nombre d'accompagnants ? ";
	cin >> nbAccomp;

	strcat(msgClient, billet);
	strcat(msgClient, ";");
	sprintf(txt, "%d", nbAccomp);
	strcat(msgClient, txt);
	strcat(msgClient, "\0");

	Send_Message(hSocketClient, msgClient, MAXSTRING, 0);
	Receive_Message(hSocketClient, msgServeur, MAXSTRING, 0);


	if(!strcmp(msgServeur, TIC)) {
		cout << "Le ticket n'est pas valide\n" << endl;
		return EXIT_FAILURE;
	} else if(!strcmp(msgServeur, NB)) {
		cout << "Le nombre de baggages est invalide\n" << endl;
		return EXIT_FAILURE;
	} else if(!strcmp(msgServeur, EMPTY)) {
		cout << "Il n'existe aucun ticket actuellement ...\n" << endl;
		return EXIT_FAILURE;
	} else {
		cout << "Connexion reussie !\n" << endl;
		return EXIT_SUCCESS;
	}
	return EXIT_FAILURE;
}

/* Envoie les infos des bagages au serveur */
bool Luggage(int hSocketClient, int luggage) {
	float poids;
	char valise[1], txt[20];
	char msgClient[100], msgServeur[100];


	for(int i = 0; i < luggage; i++) {
		cout << "Poids du bagage n°" << i << " <Enter si fini> : ";
		cin >> poids;
		cout << endl << "Valise ? ";
		cin >> valise;

		sprintf(txt, "%d", poids);
		strcat(msgClient, txt);
		strcat(msgClient, ";");
		strcat(msgClient, valise);
	}
	strcat(msgClient, "\0");

	Send_Message(hSocketClient, msgClient, MAXSTRING, 0);
	Receive_Message(hSocketClient, msgServeur, MAXSTRING, 0);

	return EXIT_FAILURE;
}
