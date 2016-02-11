#!/usr/bin/env bash
java -jar updatefx-app-1.5.jar --url=https://seagrid.org/seagrid-rich-client/site ./

cp -R site /var/www/portals/seagrid/public/seagrid-rich-client