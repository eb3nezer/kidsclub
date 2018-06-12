import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import { Location } from '@angular/common';

import { AppTitleService } from "../services/app-title.service";
import {StudentService} from "../services/student.service";
import {TeamService} from "../services/team.service";
import {Student} from "../model/student";
import {StudentTeam} from "../model/studentTeam";
import {MatSnackBar} from "@angular/material";

@Component({
  selector: 'app-edit-student',
  templateUrl: './edit-student.component.html',
  styleUrls: ['./edit-student.component.css']
})
export class EditStudentComponent implements OnInit {
    currentStudent: Student;
    fileToUpload: File;
    newFilename = "";
    projectId: number;
    allTeams: StudentTeam[];
    currentStudentTeamId: number;
    createMode = false;

    constructor(
        private appTitleService: AppTitleService,
        private studentService: StudentService,
        private route: ActivatedRoute,
        private router: Router,
        private location: Location,
        private teamService: TeamService,
        private snackBar: MatSnackBar) {
        this.currentStudent = new Student();
    }

    setFile(event) {
        if (event.srcElement.files && event.srcElement.files.length >= 1) {
            this.fileToUpload = event.srcElement.files[0];
            this.newFilename = this.fileToUpload.name;
        }
    }

    loadStudentAndTeams() {
        this.projectId = +this.route.snapshot.paramMap.get('projectId');
        if (this.projectId) {
            this.teamService.getTeamsForProject(this.projectId).subscribe(teams => this.allTeams = teams);

            const studentId: number = +this.route.snapshot.paramMap.get('studentId');

            if (studentId) {
                this.studentService.loadStudentById(studentId).subscribe(student => {
                    this.currentStudent = student;
                    if (student.studentTeam) {
                        this.currentStudentTeamId = student.studentTeam.id;
                    }
                });
                this.appTitleService.setTitle("Edit Student");
            } else {
                this.createMode = true;
                this.appTitleService.setTitle("Add Student");
                this.currentStudent = new Student();
                this.currentStudent.projectId = this.projectId;
            }
        }
    }

    onSubmit() {
        if (this.currentStudentTeamId) {
            this.currentStudent.studentTeam = new StudentTeam(this.currentStudentTeamId);
        }
        if (this.createMode) {
            this.studentService.createStudent(this.currentStudent, this.projectId).subscribe( student => {
                this.snackBar.open(`Student added`, 'Dismiss', {
                    duration: 10000,
                });
                this.router.navigate(["student", student.id], {relativeTo: this.route});
            });
        } else {
            this.studentService.updateStudent(this.currentStudent, this.fileToUpload).subscribe( student => {
                this.currentStudent = student;
                if (student.studentTeam) {
                    this.currentStudentTeamId = student.studentTeam.id;
                }
                this.snackBar.open(`Student added`, 'Dismiss', {
                    duration: 10000,
                });
            });
        }
    }

    onCancel() {
        this.location.back();
    }

    ngOnInit() {
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.loadStudentAndTeams());
    }

}
