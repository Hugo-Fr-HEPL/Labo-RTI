#include "reseaux.h"

#define DOC "DENNY_OF_CONNEXION"
#define EOC "END_OF_CONNEXION"

bool Login();

int main() {
	int ip;
	struct sockaddr_in adresse;
	properties prop = Load_Properties(FILENAME);
	struct in_addr adresseIP;

	char msgClient[MAXSTRING] = "bonjour petite peruche";
	char msgServeur[MAXSTRING];


	if(Login() == EXIT_SUCCESS) {
		ip = Create_Socket(AF_INET, SOCK_STREAM, 0);

		adresse = Infos_Host(prop);
		adresseIP = adresse.sin_addr;

		Connect_Client(ip, (struct sockaddr*)&adresse, sizeof(struct sockaddr_in));


		do {
			Send_Message(ip, msgClient, MAXSTRING, 0);
			
			if(strcmp(msgClient, EOC)) {
				Receive_Message(ip, msgServeur, MAXSTRING, 0);
			}
		} while(strcmp(msgClient, EOC) && strcmp(msgServeur, DOC));
	} else
		cout << "Impossible de se connecter\n" << endl;


	return EXIT_SUCCESS;
}


bool Login() {
	FILE* fp;
	char tmp[50];

	cout << "Encodez votre login : ";
	cin >> tmp;
	char* login = (char*)malloc(sizeof(tmp));
	strcpy(login, tmp);

	fp = fopen("login.csv", "r+t");
	if(fp != NULL) {
		fseek(fp, 0, SEEK_END);
		int size = ftell(fp);
		rewind(fp);

		char txt[200];
		fread(txt, size, 1, fp);

		char *word = NULL, *pt = NULL, *log = NULL, *pwd = NULL;
		for(int i = 1; ; i++) {
			word = Read_Line(i, txt);
			if(word == NULL) {
				cout << "Le login n'existe pas\n" << endl;
				break;
			} else {
				log = strtok_r(word, ";", &pt);

				if(strcmp(log, login) == 0) {
					char* password = NULL;

					word = NULL;
					pwd = strtok_r(word, "\0", &pt);

					while(1) {
						cout << "Encodez votre mot de passe : ";
						cin >> tmp;
						password = (char*)malloc(sizeof(tmp));
						strcpy(password, tmp);

						if(strcmp(pwd, password) == 0) {
							cout << "Connexion reussie !\n" << endl;
							return EXIT_SUCCESS;
						} else {
							cout << "Le mot de passe est incorrect\n" << endl;
							free(password);
						}
					}
				}
			}
		}
	} else
		cout << "Il n'existe aucun compte actuellement ...\n" << endl;

	return EXIT_FAILURE;
}