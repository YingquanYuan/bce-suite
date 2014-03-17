UNAME  := $(shell uname)
DARWIN := $(findstring Darwin, $(UNAME))

CC             = gcc
SHARED         = -shared
CFLAGS         = -Wall -O2 -pipe -D_REENTRANT
EXTRA_LDFLAGS  = -lgmp -lpbc
EXTRA_CPPFLAGS =
LDFLAGS        = $(EXTRA_LDFLAGS)
CPPFLAGS       = $(EXTRA_CPPFLAGS)

LIBNAME        = libbcejni
LIBEXTENSION   = so

# if on OS X
ifeq ($(DARWIN), Darwin)
	JAVA_INCDIR  = $(JAVA_HOME)/include
	LIBEXTENSION = jnilib
	CPPFLAGS    += -I$(JAVA_INCDIR)
	LDFLAGS     += -fPIC -dynamiclib -Wl,-undefined,dynamic_lookup
	SHARED       =
else
	JAVA_INCDIR  = $(JAVA_HOME)/include
	JAVA_INCDIR_PLAF = $(dir $(wildcard $(JAVA_INCDIR)/*/jni_md.h))
	CPPFLAGS    += -I$(JAVA_INCDIR) -I$(JAVA_INCDIR_PLAF)
	CFLAGS      += -ansi -pthread -fPIC -DPIC
endif

SRC_DIR     := ./src
SRCS        := $(shell find $(SRC_DIR) -name "*.c" -print)
OBJS        := $(SRCS:%.c=%.o)
TARGET_DIR := ./target

CLEAN_EXTENSIONS = o $(LIBEXTENSION)
LIBBCEJNI = $(TARGET_DIR)/$(LIBNAME).$(LIBEXTENSION)

# gcc -I/System/Library/Frameworks/JavaVM.framework/Headers -fPIC -dynamiclib -Wl, -undefined, dynamic_lookup -o libbcejni.so *.c
# gcc  -o target/libbcejni.jnilib  -lgmp -lpbc -dynamiclib -noprebind -single_module -framework JavaVM

.PHONY: all
all: mkdir $(LIBBCEJNI)

$(LIBBCEJNI): $(OBJS)
	$(CC) $(SHARED) -o $@ $^ $(LDFLAGS)

%.o: %.c
	$(CC) $(CFLAGS) $(CPPFLAGS) -c $< -o $@

.PHONY: mkdir
mkdir:
	$(shell mkdir -p $(TARGET_DIR))

.PHONY: clean
clean:
	for ext in $(CLEAN_EXTENSIONS); do \
		find . -name "*.$$ext" | xargs rm -rf ; \
	done
	rm -rf $(TARGET_DIR)
