import {AttendanceCount} from "./attendanceCount";
import {AttendanceRecord} from "./attendanceRecord";

export class AttendanceDetails {
    constructor(
        public attendanceCount?: AttendanceCount,
        public attendanceRecords?: AttendanceRecord[]
    ) {
    }
}
