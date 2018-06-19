import {StudentTeam} from "./studentTeam";
import {AttendanceRecord} from "./attendanceRecord";

export class Student {
    constructor(
        public id?: number,
        public name?: string,
        public givenName?: string,
        public familyName?: string,
        public mediaDescriptor?: string,
        public contactName?: string,
        public contactRelationship?: string,
        public phone?: string,
        public email?: string,
        public school?: string,
        public age?: number,
        public schoolYear?: string,
        public gender?: string,
        public specialInstructions?: string,
        public mediaPermitted?: boolean,
        public projectId?: number,
        public studentTeam?: StudentTeam,
        public attendanceSnapshot?: AttendanceRecord,
        public created?: number,
        public updated?: number
    ) {
    }
}
