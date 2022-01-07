GPP = g++
FLAGS = -m64 -Wall -pthread #-lnsl -lsocket
INCLUDES = -Ilib/

COMPIL = $(GPP) $(FLAGS) $(INCLUDES)

LIB = lib
BIN = bin

# Create a folder - $(@D) = the directory the current target resides in
MAKEBIN = @mkdir -p $(@D)

EXE = Client
OBJSOCKET = $(BIN)/socket.o $(BIN)/socketClient.o


ALL: $(EXE)


# Executable
Client: $(OBJSOCKET) Application_CIAChat.cpp Application_CIAChat.h
	$(COMPIL) $(OBJSOCKET) -o Client Application_CIAChat.cpp
	echo Created: Client


# Objects
$(BIN)/%.o: $(LIB)/%.cpp $(LIB)/%.h
	$(MAKEBIN)
	$(MAKECONF)
	@$(COMPIL) -c $< -o $@
	echo Created: socket.o, socketClient.o