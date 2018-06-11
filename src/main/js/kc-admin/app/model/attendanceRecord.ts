import {Student} from "./student";
import {User} from "./user";

export class AttendanceRecord {
    constructor(
        public id?: number,
        public student?: Student,
        public attendanceType?: string,
        public attendanceCode?: string,
        public recordTime?: string,
        public verifier?: User,
        public comment?: string
    ) {
    }
}
