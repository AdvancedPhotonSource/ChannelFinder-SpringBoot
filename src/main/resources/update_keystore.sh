#!/bin/bash

CALL_DIR=`pwd`
MY_DIR=`dirname $0` && cd $MY_DIR && MY_DIR=`pwd`

if [ ! -z "$1" ]; then
    KEY_FILE=$1
else
    echo "Please provide a key file input."
    exit 2
fi

if [ ! -z "$2" ]; then
    CRT_FILE=$2
else
    >&2 echo "Please provide a crt file input."
    exit 2
fi

echo -n "Please enter the master password for : "
read -s masterPassword
echo ""; 

if [ -z $masterPassword ]; then
    >&2 echo "A master password must be provided"
    exit 2
fi


KEYSTORE_PATH=$MY_DIR/keystore/keystore.pkcs12

# Test entered password
failed=0
keytool -list -v -keystore $KEYSTORE_PATH --storepass $masterPassword > /dev/null || failed=1

if [ $failed == 1 ]; then
    >&2 echo "The password entered was incorrect"
    exit 2
fi

PKCS12_CERT_STORE="/tmp/${RANDOM}cert.pkcs12"
SSL_ALIAS="tomcat"

echo "Creating keystore: $PKCS12_CERT_STORE"
openssl pkcs12 -export -in $CRT_FILE -inkey $KEY_FILE -out $PKCS12_CERT_STORE -name $SSL_ALIAS -passout pass:$masterPassword

echo "Deleting old certs from $KEYSTORE_PATH"
keytool -keystore $KEYSTORE_PATH -delete -alias $SSL_ALIAS -storepass $masterPassword

keytool -importkeystore \
-srckeystore $PKCS12_CERT_STORE \
-srcstoretype pkcs12 \
-srcstorepass $masterPassword \
-deststoretype pkcs12 \
-destkeystore $KEYSTORE_PATH \
-deststorepass $masterPassword


echo "Removing keystore: $PKCS12_CERT_STORE"
rm $PKCS12_CERT_STORE 

