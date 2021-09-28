#include "LibReseaux.h"

int Create_Socket(int ipaddr, int type, int protocole)
{
	int handsock;
	handsock = socket(ipaddr,type,protocole);
	if(handsock==-1)
	{
		printf("Erreur de creation de la socket %d \n",errno);
		exit(1);
	}
	printf("socket n°: %d \n",handsock);
	return handsock;
}
void Bind_Socket(int handle, const struct sockaddr *adresse, int taille)
{
	int reussite;
	reussite = bind(handle, adresse, taille);
	if(reussite==-1)
	{
		printf("Erreur sur le bind de la socket %d \n",errno);
		exit(1);
	}
}
void Listen_Serveur(int handle, int nbrconnexion)
{
	int reussite;
	reussite = listen(handle, nbrconnexion);
	if(reussite==-1)
	{
		printf("Erreur sur l'ecoute %d \n",errno);
		//close(handle);
		exit(1);
	}
}
void Connect_Client(int handle, struct sockaddr *adresse, int taille)
{
	int reussite;
	reussite = connect(handle, adresse, taille);
	if(reussite==-1)
	{
		printf("Erreur de connexion %d \n",errno);
		//close(handle);
		exit(1);
	}
}
int Accept_Serveur(int handle, struct sockaddr *adresse, int taille)
{
	int HSocketService;
	unsigned int t = taille;
	HSocketService = accept(handle,adresse,&t);
	if(HSocketService==-1)
	{
		printf("Erreur sur l'acceptation %d \n",errno);
		//close(handle);
		exit(1);
	}
	return HSocketService;
}
void Send_Message(int handleCible, const void* message, int taillemessage, int flagUrg)
{
	int reussite;
	reussite = send(handleCible, message, taillemessage, flagUrg);
	if(reussite==-1)
	{
		printf("Erreur sur le send %d \n",errno);
		exit(1);
	}
}
void Receive_Message(int handleSource, void* message, int taillemessage, int flagUrgdest)
{
	int reussite;
	reussite = recv(handleSource, message, taillemessage, flagUrgdest);
	if(reussite==-1)
	{
		printf("Erreur sur le receive %d \n",errno);
		exit(1);
	}
	printf("message recu: %s\n",(char*)message);
}



properties Load_Properties(const char* nomFichier)
{
	properties prop;
	FILE *fp;

	char hostname[200];
	gethostname(hostname, 200);

	fp = fopen(nomFichier, "r+t");
	if(fp == NULL)
	{
		fp = fopen(nomFichier, "w+t");
		prop.nomMachine = (char*)malloc(strlen(hostname));
		strcpy(prop.nomMachine, hostname);
		prop.port = 50000;

		fprintf(fp, "Machine:");
		fprintf(fp, prop.nomMachine, sizeof(prop.nomMachine));
		fprintf(fp, ";\r");
		fprintf(fp, "Port:50000;");
	}
	else
	{
		fseek(fp, 0, SEEK_END);
		int size = ftell(fp);
		rewind(fp);
		
		char txt[200];
		char res[200];
		char res1[200];
		fread(txt, size, 1, fp);
		Read_Lines(1,txt,res);
		Read_Lines(2,txt,res1);

		prop.nomMachine = (char*)malloc(strlen(res));
		strcpy(prop.nomMachine, res);

		prop.port = atoi(res1);
	}
	fclose(fp);
	return prop;
}
void Read_Lines(int ligne, char* txt, char* ret)
{
	char l;
	int i=0, j=0;
	while(i<ligne)
	{
		l=*txt;
		if(l==':')
		{
			i++;
			txt++;
		}
		else
		{
			txt++;
		}
	}
	i=0;
	while(i<1)
	{
		l=*txt;
		if(l==';')
		{
			i++;
		}
		else
		{
			*(ret+j)=*txt;
			j++;
			txt++;
		}
	}
	*(ret+j)='\0';
}
