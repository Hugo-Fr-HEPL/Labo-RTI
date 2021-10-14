#include "application_checkin.h"


int main() {
	char msgServeur[MAXSTRING];


// Connexion au serveur
	properties prop = sock.Load_Properties(PROPFILE);
	cli.hSock = sock.Create_Socket(AF_INET, SOCK_STREAM, 0);
	struct sockaddr_in adresse = sock.Infos_Host(prop);
	//struct in_addr adresseIP = adresse.sin_addr;
	sock.Connect_Client(cli.hSock, (struct sockaddr*)&adresse);


// Vérification si assez de place
	if(sock.Receive_Message(cli.hSock, msgServeur, 0) == EXIT_FAILURE)
		CloseConnection();


	MainLoop();


	CloseConnection();
}


/*
	Boucle à travers toutes les actions du client
*/
void MainLoop() {
	char msgClient[MAXSTRING];
	int choice = 0;

	do {
		cout << "Que voulez-vous faire ?" << endl;
		if(choice == 0)
			cout << "1. Se connecter" << endl;
		if(choice >= 1) {
			cout << "1. Changer de compte" << endl;
			cout << "2. Garder le même compte" << endl;
		}
		cout << "0. Fermer l'application" << endl;
		cin >> choice;

// Reset Values
		cli.ticket = NULL;


// Connexion
		if(choice == 0) break;
		if(choice == 1) {
			sprintf(msgClient, "%d", LOGIN_OFFICER);
			sock.Send_Message(cli.hSock, msgClient, 0);
			while(Login() != EXIT_SUCCESS);
		}

// Encoder ticket
		sprintf(msgClient, "%d", CHECK_TICKET);
		sock.Send_Message(cli.hSock, msgClient, 0);
		while(Ticket() != EXIT_SUCCESS);

//Encoder bagages
		sprintf(msgClient, "%d", CHECK_LUGGAGE);
		sock.Send_Message(cli.hSock, msgClient, 0);
		Luggage();

// Paiement
		sprintf(msgClient, "%d", PAYMENT_DONE);
		sock.Send_Message(cli.hSock, msgClient, 0);
		Payment();

		cout << endl << "Nouveau ticket :" << endl;
	} while(1);
}


/*
	Envoie ses infos de connexion au serveur
	return EXIT_SUCCESS on success
*/
bool Login() {
	char msgClient[MAXSTRING] = "", msgServeur[MAXSTRING];
	char login[30], password[30];


	cout << "Encodez votre login : ";
	cin >> login;
	cout << "Encodez votre mot de passe : ";
	cin >> password;

	strcat(msgClient, login);
	strcat(msgClient, ";");
	strcat(msgClient, password);
	strcat(msgClient, "\0");

	sock.Send_Message(cli.hSock, msgClient, 0);
	if(sock.Receive_Message(cli.hSock, msgServeur, 0) == EXIT_FAILURE)
		CloseConnection();

	return ShowMessage(msgServeur);
}


/*
	Envoie les billets au serveur
	Renvoie le ticket ou NULL
*/
bool Ticket() {
	char msgClient[MAXSTRING] = "", msgServeur[MAXSTRING];
	char tmp[30];

// Encoder
	cout << "Numero de billet ? ";
	cin >> tmp;

	strcat(msgClient, tmp);
	strcat(msgClient, ";");
	strcat(msgClient, "\0");

	sock.Send_Message(cli.hSock, msgClient, 0);
	if(sock.Receive_Message(cli.hSock, msgServeur, 0) == EXIT_FAILURE)
		CloseConnection();

	if(ShowMessage(msgServeur) == EXIT_SUCCESS) {
		cli.ticket = (char*)malloc(sizeof(tmp));
		strcpy(cli.ticket, tmp);
		return EXIT_SUCCESS;
	}
	return EXIT_FAILURE;
}


/*
	Envoie les infos des bagages au serveur
*/
void Luggage() {
	char msgClient[MAXSTRING] = "", msgServeur[MAXSTRING];
	float poids;
	char valise, txt[20] = "";

	cout << "Nombre d'accompagnants ? ";
	cin >> cli.accomp;
	cout << "Nombre de bagages ? ";
	cin >> cli.lug;

	for(int i = 1; i <= cli.lug; i++) {
		cout << "Poids du bagage n°" << i << " : ";
		cin >> poids;
		do {
			cout << "Valise ? " << endl;
			cout << "O. Oui | N. Non" << endl;
			cin >> valise;
		} while(valise != 'O' && valise != 'N');

		sprintf(txt, "%f", poids);
		strcat(msgClient, txt);
		strcat(msgClient, ";");
		strcat(msgClient, &valise);
		strcat(msgClient, ";");
	}
	strcat(msgClient, "\0");

	sock.Send_Message(cli.hSock, msgClient, 0);
	if(sock.Receive_Message(cli.hSock, msgServeur, 0) == EXIT_FAILURE)
		CloseConnection();

	cli.supp = atoi(msgServeur);
}


/*
	Valide le paiement
*/
void Payment() {
	char msgServeur[MAXSTRING];
	char choice = 'O';

	if(cli.supp > 0) {
		cout << "Prix du supplement de bagages : " << cli.supp << " EUR" << endl;
		do {
			cout << "Payer le supplement ? " << endl;
			cout << "O. Oui | N. Non" << endl;
			cin >> choice;
		} while(choice != 'O' && choice != 'N');
	} else
		cout << "Pas de supplement a payer" << endl;

	if(choice == 'O')
		sock.Send_Message(cli.hSock, cli.ticket, 0);
	else
		sock.Send_Message(cli.hSock, "N", 0);

	sock.Receive_Message(cli.hSock, msgServeur, 0);
	if(strcmp(msgServeur, EOC) == 0)
		CloseConnection();
}


/*
	Ferme tout proprement
*/
void CloseConnection() {
	close(cli.hSock);
    printf("Socket client ferme\n");
	exit(EXIT_FAILURE);
}


/*
	Display a message depending of the return type
*/
bool ShowMessage(char* msg) {
	switch(atoi(msg)) {
	case LOG:
		cout << "Le login n'existe pas" << endl;
		break;
	case PWD:
		cout << "Le mot de passe est incorrect" << endl;
		break;
	case EMPTY:
		cout << "Il n'existe aucun ticket actuellement ..." << endl;
		break;
	case TIC:
		cout << "Le ticket n'est pas valide" << endl;
		break;
	case OK:
		cout << "Connexion reussie !" << endl;
		return EXIT_SUCCESS;
		break;
	default:
		cout << "Erreur inconnue" << endl;
		break;
	}
	return EXIT_FAILURE;
}