import { Component, OnInit } from '@angular/core';
import { AppTitleService } from "../../shared/services/app-title.service";
import { ProjectService } from "../../shared/services/project.service";
import { Project } from "../../shared/model/project";
import {ActivatedRoute, Router} from "@angular/router";
import {Student} from "../../shared/model/student";
import {StudentService} from "../../shared/services/student.service";
import {AttendanceRecord} from "../../shared/model/attendanceRecord";
import {AttendanceService} from "../../shared/services/attendance.service";

@Component({
  selector: 'view-student',
  templateUrl: './student.component.html',
  styleUrls: ['./student.component.css']
})
export class StudentComponent implements OnInit {
    project: Project;
    student: Student;
    attendance: AttendanceRecord[];
    displayedColumns = ['time', 'status', 'who', 'comments'];

    constructor(
        private appTitleService: AppTitleService,
        private route: ActivatedRoute,
        private router: Router,
        private projectService: ProjectService,
        private studentService: StudentService,
        private attendanceService: AttendanceService
    ) {
    }

    loadProjectAndStudents() {
        const projectId = +this.route.snapshot.paramMap.get('projectId');
        if (projectId) {
            this.projectService.getProject(projectId).subscribe(project => {
                this.project = project;
                this.appTitleService.setCurrentProject(project);
            });
            const studentId = +this.route.snapshot.paramMap.get('studentId');

            this.studentService.loadStudentById(studentId).subscribe(student => {
                this.student = student;
                Promise.resolve(null).then(() => this.appTitleService.setTitle(student.name));
            });

            this.attendanceService.getAllAttendanceForStudent(projectId, studentId).subscribe(attendance => this.attendance = attendance);
        }
    }

    ngOnInit() {
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.loadProjectAndStudents());
    }
}
