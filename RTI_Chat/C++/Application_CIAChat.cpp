#include "Application_CIAChat.h"


int main() {
	char msgServeur[MAXSTRING];


// Connexion au serveur
	properties prop = sock.Load_Properties(PROPFILE);
	cli.hSock = sock.Create_Socket(AF_INET, SOCK_STREAM, 0);
	struct sockaddr_in adresse = sock.Infos_Host(prop);
	//struct in_addr adresseIP = adresse.sin_addr;
	sock.Connect_Client(cli.hSock, (struct sockaddr*)&adresse);


	Login();


// Récupération de l'adresse et du port UDP
	if(sock.Receive_Message(cli.hSock, msgServeur, 0) == EXIT_FAILURE)
		CloseConnection();

cout << "test " << msgServeur << endl;
	//MainLoop();


	CloseConnection();
}


/*
	Envoie ses infos de connexion au serveur
	return EXIT_SUCCESS on success
*/
void Login() {
	char msgClient[MAXSTRING] = "  ";
	char login[30] = "Blair", password[30] = "mdpBlair";

/*
	char login[30], password[30];
	cout << "Encodez votre login : ";
	cin >> login;
	cout << "Encodez votre mot de passe : ";
	cin >> password;
*/

// Message for the server to know it's a C application
	strcat(msgClient, LOGIN_GROUP);
	strcat(msgClient, "#");
	strcat(msgClient, LOGIN_C);
	strcat(msgClient, "#");
	strcat(msgClient, login);
	strcat(msgClient, "#");
	strcat(msgClient, password);
	strcat(msgClient, "$");


	sock.Send_Message(cli.hSock, msgClient, 0);
}


/*
	Ferme tout proprement
*/
void CloseConnection() {
	close(cli.hSock);
    printf("Socket client ferme\n");
	exit(EXIT_FAILURE);
}