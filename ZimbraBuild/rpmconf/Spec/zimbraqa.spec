#
# spec file for zimbra.rpm
#
Summary: Liquid QA Tests
Name: zimbra-qatest
Version: @@VERSION@@
Release: @@RELEASE@@
Copyright: Copyright 2005 Zimbra, Inc.
Group: Applications/Messaging
URL: http://www.zimbra.com
Vendor: Zimbra, Inc.
Packager: Zimbra, Inc.
BuildRoot: /opt/zimbra
AutoReqProv: no
requires: zimbra-core

%description
Best email money can buy

%prep

%build

%install

%pre

%post
chown -R zimbra:zimbra /opt/zimbra/qa
chmod a+x /opt/zimbra/qa/scripts/*

%preun

%files
