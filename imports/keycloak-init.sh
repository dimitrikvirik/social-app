#!/bin/bash

set -e

# Set environment variables
export KEYCLOAK_URL="http://localhost:8080/auth"
export KEYCLOAK_REALM="master"
export KEYCLOAK_USER="admin"
export KEYCLOAK_PASSWORD="admin"

# Import realm
/opt/jboss/keycloak/bin/kcadm.sh config credentials --server $KEYCLOAK_URL --realm $KEYCLOAK_REALM --user $KEYCLOAK_USER --password $KEYCLOAK_PASSWORD
/opt/jboss/keycloak/bin/kcadm.sh create realms -f /opt/jboss/keycloak/imports/realm-export.json
