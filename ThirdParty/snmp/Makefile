SNMP_ROOT := $(shell pwd)
P4_ROOT ?= $(shell cd $(SNMP_ROOT)/../..; pwd)
MAKE ?= make
MAKEARGS ?= -j2

BUILD_PLATFORM ?= $(shell sh $(P4_ROOT)/ZimbraBuild/rpmconf/Build/get_plat_tag.sh)
ZIMBRA_HOME ?= /opt/zimbra

ifdef BETA
	include $(SNMP_ROOT)/../beta_versions.def
else
	include $(SNMP_ROOT)/../versions.def
endif

SNMP_NAME=net-snmp
SNMP_VERSION ?= 5.5
APP_TGZ_TARGET := $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/snmp/$(SNMP_NAME)-$(SNMP_VERSION).tgz
INSTALL_PREFIX := $(ZIMBRA_HOME)/$(SNMP_NAME)-$(SNMP_VERSION)

PERL_DEST_DIR=$(SNMP_ROOT)/../Perl/zimbramon
PERL_DEST_LIB_DIR=$(PERL_DEST_DIR)/lib

PATCH	:=
ifeq ($(BUILD_PLATFORM), )
	BUILD_PLATFORM := "UNKNOWN"
endif
ifeq ($(BUILD_PLATFORM), RHEL7_64)
	ENVMOD := env LIBS="-L/usr/lib64"
endif
ifeq ($(BUILD_PLATFORM), RHEL6_64)
	ENVMOD := env LIBS="-L/usr/lib64"
endif
ifeq ($(BUILD_PLATFORM), CentOS6_64)
	ENVMOD := env LIBS="-L/usr/lib64"
endif

LDFLAGS	:= LDFLAGS="-L$(OPENSSL_LIB_DIR) -R$(OPENSSL_LIB_DIR)"
ifeq ($(BUILD_PLATFORM), UBUNTU12_64)
LDFLAGS	:= LDFLAGS="-L$(OPENSSL_LIB_DIR) -Wl,-rpath,$(OPENSSL_LIB_DIR)"
endif
ifeq ($(BUILD_PLATFORM), RHEL7_64)
LDFLAGS	:= LDFLAGS="-L$(OPENSSL_LIB_DIR) -Wl,-rpath,$(OPENSSL_LIB_DIR)"
endif

files   := $(wildcard src/$(SNMP_NAME)-$(SNMP_VERSION).tar.gz)

all: checksrc allclean build tar

checksrc:
	$(if $(files), @echo "", exit 1)

build: $(SNMP_NAME)-$(SNMP_VERSION)
	(cd $(SNMP_NAME)-$(SNMP_VERSION); \
	$(ENVMOD) CFLAGS="-g -O2" \
	$(LDFLAGS) \
	LD_LIBRARY_PATH=$(OPENSSL_LIB_DIR) \
 	./configure --prefix=$(INSTALL_PREFIX) \
	--with-default-snmp-version=3 --with-sys-contact="admin" \
	--with-sys-location="unknown" --with-logfile="$(ZIMBRA_HOME)/log/snmpd.log" \
	--with-openssl=$(ZIMBRA_HOME)/openssl-$(OPENSSL_VERSION) \
	--disable-embedded-perl \
	--with-perl-modules="PREFIX=$(PERL_DEST_DIR) LIB=$(PERL_DEST_LIB_DIR)" \
	--with-persistent-directory="$(INSTALL_PREFIX)/var"; \
	$(MAKE) ; $(MAKE) install)
	(cp -f $(SNMP_NAME)-$(SNMP_VERSION)/COPYING $(INSTALL_PREFIX)/LICENSE)
	(mkdir -p $(INSTALL_PREFIX)/var)
	(cd $(ZIMBRA_HOME); ln -s $(SNMP_NAME)-$(SNMP_VERSION) $(SNMP_NAME))

$(SNMP_NAME)-$(SNMP_VERSION): 
	tar xzf src/$(SNMP_NAME)-$(SNMP_VERSION).tar.gz

tar:
	mkdir -p $(P4_ROOT)/ThirdPartyBuilds/$(BUILD_PLATFORM)/snmp
	(cd $(ZIMBRA_HOME); tar czf $(APP_TGZ_TARGET) $(SNMP_NAME)-$(SNMP_VERSION))

p4edit: $(APP_TGZ_TARGET)
	p4 add $(APP_TGZ_TARGET)
	p4 edit $(APP_TGZ_TARGET)
	

clean:
	/bin/rm -rf $(SNMP_NAME)-$(SNMP_VERSION)

allclean: clean
	/bin/rm -rf $(ZIMBRA_HOME)/$(SNMP_NAME)-$(SNMP_VERSION)
	/bin/rm -rf $(ZIMBRA_HOME)/$(SNMP_NAME)
	rm -f $(APP_TGZ_TARGET)
