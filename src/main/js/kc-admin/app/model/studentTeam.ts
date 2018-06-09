import {Project} from "./project";
import {User} from "./user";
import {Student} from "./student";

export class StudentTeam {
    constructor(
        public id?: number,
        public project?: Project,
        public name?: string,
        public score?: number,
        public created?: number,
        public updated?: number,
        public leaders?: User[],
        public students?: Student[],
        public avatarUrl?: string,
        public mediaDescriptor?: string
    ) {

    }
}
