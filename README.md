# KC
A web app for managing children's holiday programs

## Getting Started

You will need a PostreSQL database to run KC. You should
create a database, along with a user that has permission to create
tables in the DB.

## Environment Variables

Everything is configured through src/main/resources/application.yaml.

* DB_USER - The user name for PostgreSQL
* DB_PASSWORD - The password for PostgreSQL
* DB_URL - The JDBC URL to your database
* ADMIN_EMAIL - A user account will always be created for this
email address. This allows the admin to log in for the first time.
* KEY_FILE - The path to a suitable keystore.p12 file
* KEY_FILE_PASSWORD - The password to your kestore
* GOOGLE_CLIENT_ID - The ID for Google OAuth2
* GOOGLE_CLIENT_SECRET - The client secret for Google OAuth2
## Building

KC uses a standard Maven build.

## Starting

KC uses Spring Boot. You either start it in your IDE,
or with "java -jar kidsclub.jar"

## License

KC is Copyright 2018 Ben Kelley.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.