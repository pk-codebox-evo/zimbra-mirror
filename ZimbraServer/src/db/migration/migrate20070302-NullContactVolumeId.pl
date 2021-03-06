#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite Server
# Copyright (C) 2007, 2008, 2009, 2010, 2013 Zimbra Software, LLC.
# 
# The contents of this file are subject to the Zimbra Public License
# Version 1.4 ("License"); you may not use this file except in
# compliance with the License.  You may obtain a copy of the License at
# http://www.zimbra.com/license.
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
# ***** END LICENSE BLOCK *****
# 


use strict;
use Migrate;

foreach my $group (Migrate::getMailboxGroups()) {
    nullContactVolumeId($group);
}

exit(0);

#####################

sub nullContactVolumeId($) {
  my ($group) = @_;

  my $sql = <<NULL_CONTACT_VOLUME_ID_EOF;
UPDATE $group.mail_item
SET volume_id = NULL
WHERE type = 6;
NULL_CONTACT_VOLUME_ID_EOF

  Migrate::runSql($sql);
}
