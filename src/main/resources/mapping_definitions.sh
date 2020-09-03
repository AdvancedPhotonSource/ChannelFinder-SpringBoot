#!/bin/sh

es_host=localhost
es_port=9200

###
# #%L
# ChannelFinder Directory Service
# %%
# Copyright (C) 2010 - 2016 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
# %%
# Copyright (C) 2010 - 2012 Brookhaven National Laboratory
# All rights reserved. Use is subject to license terms.
# #L%
###
# The mapping definition for the Indexes associated with the channelfinder v4

# To delete all data and Indexes.
# curl -XDELETE localhost:9200/*

#Create the Index
curl -H 'Content-Type: application/json' -XPUT http://${es_host}:${es_port}/cf_tags -d'
{
"mappings":{
  "cf_tag" : {
    "properties" : {
      "name" : {
        "type" : "keyword"
      },
      "owner" : {
        "type" : "keyword"
      }
    }
    }
  }
}'

curl -H 'Content-Type: application/json' -XPUT http://${es_host}:${es_port}/cf_properties -d'
{
"mappings":{
  "cf_property" : {
    "properties" : {
      "name" : {
        "type" : "keyword"
      },
      "owner" : {
        "type" : "keyword"
      }
    }
  }
  }
}'

curl -H 'Content-Type: application/json' -XPUT http://${es_host}:${es_port}/channelfinder -d'
{
"mappings":{
  "cf_channel" : {
    "properties" : {
      "name" : {
        "type" : "keyword"
      },
      "owner" : {
        "type" : "keyword"
      },
      "script" : {
        "type" : "keyword"
      },
      "properties" : {
        "type" : "nested",
        "properties" : {
          "name" : {
            "type" : "keyword"
          },
          "owner" : {
            "type" : "keyword"
          },
          "value" : {
            "type" : "keyword"
          }
        }
      },
      "tags" : {
        "type" : "nested",
        "properties" : {
          "name" : {
            "type" : "keyword"
          },
          "owner" : {
            "type" : "keyword"
          }
        }
      }
    }
  }
  },
"settings": {
  "index": {
    "max_result_window": 10000
  }
}
}'
#Default max_result_window is 10000
