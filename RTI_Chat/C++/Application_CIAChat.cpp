#include "Application_CIAChat.h"


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

	if(strcmp(msgServeur, NO_CO) == 0) {
		cout << "Plus de connexions disponibles.." << endl;
		CloseConnection();
	}


	MainLoop();


	CloseConnection();
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