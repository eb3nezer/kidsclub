import {User} from "./user";

export class Project {
    constructor(public id?: number,
                public name?: string,
                public disabled?: boolean,
                public users?: User[],
                public properties?: any) {
    }
}
