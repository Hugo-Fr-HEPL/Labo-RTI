OBJET = reseaux.o
GPP = g++ -m64 -Wall -D CPP -pthread -Ilib/ #-lnsl -lsocket

ALL: Serv AppCheck


# Executable
Serv: $(OBJET) serveur_checkin.cpp
	echo Creation de Serv
	$(GPP) -o Serv serveur_checkin.cpp $(OBJET)
AppCheck: $(OBJET) application_checkin.cpp
	echo Creation de AppCheck
	$(GPP) -o AppCheck application_checkin.cpp $(OBJET)

# Deprecated Tests
TEST: Serveur Client
Serveur: $(OBJET) serveur.cpp
	echo Creation de Serveur
	$(GPP) -o Serveur serveur.cpp $(OBJET)

Client: $(OBJET) client.cpp
	echo Creation de Client
	$(GPP) -o Client client.cpp $(OBJET)


# Objects
reseaux.o: lib/reseaux.cpp lib/reseaux.h
	echo Creation de reseaux.o
	$(GPP) lib/reseaux.cpp -c 