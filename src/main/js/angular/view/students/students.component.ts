import { Component, OnInit } from '@angular/core';
import { AppTitleService } from "../../shared/services/app-title.service";
import { ProjectService } from "../../shared/services/project.service";
import { Project } from "../../shared/model/project";
import {ActivatedRoute, Router} from "@angular/router";
import {Student} from "../../shared/model/student";
import {StudentService} from "../../shared/services/student.service";
import {MatButtonToggleChange} from "@angular/material/button-toggle";
import {MatButtonToggleGroup} from "@angular/material/button-toggle";

@Component({
  selector: 'view-students',
  templateUrl: './students.component.html',
  styleUrls: ['./students.component.css']
})
export class StudentsComponent implements OnInit {
    project: Project;
    students: Student[];
    allStudents: Student[];
    displayedColumns = ['name', 'warnings', 'team', 'attendance'];
    matButtonToggleGroup: MatButtonToggleGroup;

    constructor(
        private appTitleService: AppTitleService,
        private route: ActivatedRoute,
        private router: Router,
        private projectService: ProjectService,
        private studentService: StudentService
    ) {
    }

    public filter(event: MatButtonToggleChange) {
        if (event && event.value) {
            if (event.value === "all") {
                this.students = this.allStudents;
            } else if (event.value === "none") {
                this.students = [];
                for (let i = 0; i < this.allStudents.length; i++) {
                    if (!this.allStudents[i].attendanceSnapshot) {
                        this.students.push(this.allStudents[i]);
                    }
                }
            } else if (event.value === "signedin") {
                this.students = [];
                for (let i = 0; i < this.allStudents.length; i++) {
                    if (this.allStudents[i].attendanceSnapshot && this.allStudents[i].attendanceSnapshot.attendanceCode == 'I') {
                        this.students.push(this.allStudents[i]);
                    }
                }
            }
        }
    }

    loadProjectAndStudents() {
        const projectId = +this.route.snapshot.paramMap.get('id');
        if (projectId) {
            this.projectService.getProject(projectId).subscribe(project => {
                this.project = project;
                this.appTitleService.setCurrentProject(project);
            });

            this.studentService.loadStudentsForProject(projectId).subscribe(students => {
                if (students) {
                    this.students = students;
                    this.allStudents = students;
                }
            });
        }
    }

    ngOnInit() {
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.appTitleService.setTitle("Students"));
        Promise.resolve(null).then(() => this.loadProjectAndStudents());
    }
}
