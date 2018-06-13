import {Project} from "./project";

export class KCDocument {
    constructor(
        public id?: number,
        public name?: string,
        public description?: string,
        public mediaDescriptor?: string,
        public contentType?: string,
        public project?: Project,
        public icon?: string,
        public created?: string,
        public updated?: string
    ) {
    }
}
