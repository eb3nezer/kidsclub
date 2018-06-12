import {Project} from "./project";
import {AlbumItem} from "./albumItem";

export class Album {
    constructor(
        public id?: number,
        public name?: string,
        public description?: string,
        public project?: Project,
        public items?: AlbumItem[],
        public shared?: boolean,
        public created?: number,
        public updated?: number
    ) {
    }
}
