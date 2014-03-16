CC			:=	gcc
CFLAGS		:=	-Wall -lgmp -lpbc

SRC_DIR		:=	./src

OBJS		:=	pbc_bce.o bce_utils.o bcejni.o main.o
TARGET_DIR	:=	./target
TARGET_OBJ	:=	$(addprefix $(TARGET_DIR)/, libbcejni.so)

.PHONY: build
build: mkdir $(TARGET_DIR)/$(TARGET_OBJ)

$(TARGET_DIR)/$(TARGET_OBJ): $(TARGET_OBJS):
	$(CC) $^ $(CFLAGS) -o $@

.PHONY: all
all: clean build

.PHONY: mkdir
mkdir:
	$(shell mkdir -p $(TARGET_DIR))

.PHONY: clean
clean:
	rm -rf $(TARGET_DIR)
