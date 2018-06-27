import {ImageCollection} from "./imageCollection";

export class AlbumItem {
    constructor(
        public id?: number,
        public order?: number,
        public description?: string,
        public imageCollection?: ImageCollection,
        public mediaDescriptor?: string,
        public created?: string,
        public updated?: string
    ) {
    }
}
