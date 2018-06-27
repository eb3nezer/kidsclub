import {ImageCollection} from "./imageCollection";

export class User {
    constructor(
        public id?: number,
        public name?: string,
        public givenName?: string,
        public familyName?: string,
        public email?: string,
        public mobilePhone?: string,
        public homePhone?: string,
        public avatarUrl?: string,
        public mediaDescriptor?: string,
        public loggedIn?: boolean,
        public active?: boolean,
        public imageCollection?: ImageCollection
    ) {
    }
}
