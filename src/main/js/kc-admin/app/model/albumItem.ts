export class AlbumItem {
    constructor(
        public id?: number,
        public order?: number,
        public description?: string,
        public mediaDescriptor?: string,
        public created?: number,
        public updated?: number
    ) {
    }
}
