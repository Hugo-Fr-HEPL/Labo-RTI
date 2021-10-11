#include "application_checkin.h"


int main() {
	//char msgClient[MAXSTRING], msgServeur[MAXSTRING];


	properties prop = sock.Load_Properties(PROPFILE);

	hSocketClient = sock.Create_Socket(AF_INET, SOCK_STREAM, 0);

	struct sockaddr_in adresse = sock.Infos_Host(prop);
	//struct in_addr adresseIP = adresse.sin_addr;

	sock.Connect_Client(hSocketClient, (struct sockaddr*)&adresse);


// Connexion
	char* send = (char*)malloc(sizeof(int));
	sprintf(send, "%d", LOGIN_OFFICER);
	sock.Send_Message(hSocketClient, send, 0);
	while(Login() != EXIT_SUCCESS);


	do {
// Encoder ticket
		sprintf(send, "%d", CHECK_TICKET);
		sock.Send_Message(hSocketClient, send, 0);
		char* ticket = NULL;
		while((ticket = Ticket()) == NULL);

//Encoder bagages
		sprintf(send, "%d", CHECK_LUGGAGE);
		sock.Send_Message(hSocketClient, send, 0);
		int total = Luggage();

// Paiement
		sprintf(send, "%d", PAYMENT_DONE);
		sock.Send_Message(hSocketClient, send, 0);
		Payment(total, ticket);

		cout << endl << "Nouveau ticket :" << endl;
	} while(1);

//	do {
//		Send_Message(hSocketClient, msgClient, 0);

//		if(strcmp(msgClient, EOC)) {
//			Receive_Message(hSocketClient, msgServeur, 0);

//			strcpy(msgClient, EOC);
//		}
//	} while(strcmp(msgClient, EOC) && strcmp(msgServeur, DOC));

	CloseConnection(hSocketClient);
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

	sock.Send_Message(hSocketClient, msgClient, 0);
	sock.Receive_Message(hSocketClient, msgServeur, 0);

	return ShowMessage(msgServeur);
}


/*
	Envoie les billets au serveur
	Renvoie le ticket ou NULL
*/
char* Ticket() {
	char msgClient[MAXSTRING] = "", msgServeur[MAXSTRING];
	char tmp[30];

// Encoder
	cout << "Numero de billet ? ";
	cin >> tmp;

	strcat(msgClient, tmp);
	strcat(msgClient, ";");
	strcat(msgClient, "\0");

	sock.Send_Message(hSocketClient, msgClient, 0);
	sock.Receive_Message(hSocketClient, msgServeur, 0);

	if(ShowMessage(msgServeur) == EXIT_SUCCESS) {
		char* billet = (char*)malloc(sizeof(tmp));
		strcpy(billet, tmp);
		return billet;
	}
	return NULL;
}


/*
	Envoie les infos des bagages au serveur
*/
int Luggage() {
	char msgClient[MAXSTRING] = "", msgServeur[MAXSTRING];
	float poids;
	int nombrebag;
	char valise, txt[20] = "";
	
	cout << "Nombre de bagages ? ";
	cin >> nombrebag;

	for(int i = 1; i <= nombrebag; i++) {
		cout << "Poids du bagage n°" << i << " (Enter si fini) : ";
		cin >> poids;
		do {
			cout << "Valise (oui = O, non = N)? ";
			cin >> valise;
		} while(valise != 'O' && valise != 'N');

		sprintf(txt, "%f", poids);
		strcat(msgClient, txt);
		strcat(msgClient, ";");
		strcat(msgClient, &valise);
		strcat(msgClient, ";");
	}
	strcat(msgClient, "\0");

	sock.Send_Message(hSocketClient, msgClient, 0);
	sock.Receive_Message(hSocketClient, msgServeur, 0);

	return atoi(msgServeur);
}


/*
	Valide le paiement
*/
bool Payment(int supplement, char* ticket) {
	char msgServeur[MAXSTRING];

	if(supplement > 0) {
		cout << "Prix du supplement de bagages : " << supplement << " EUR" << endl;
		cout << "Enter pour payer" << endl << endl;
	} else
		cout << "Pas de supplement a payer" << endl;
	cin.ignore();

	sock.Send_Message(hSocketClient, ticket, 0);
	sock.Receive_Message(hSocketClient, msgServeur, 0);

	return EXIT_SUCCESS;
}


/*
	Ferme tout proprement
*/
void CloseConnection(int hSocketClient) {
	close(hSocketClient);
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