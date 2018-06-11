import { Component, OnInit } from '@angular/core';
import { Observable } from "rxjs/index";
import { FormControl } from "@angular/forms";
import { ActivatedRoute } from "@angular/router";
import { AppTitleService } from "../services/app-title.service";
import { ProjectService } from "../services/project.service";
import { TeamService } from "../services/team.service";
import { StudentService } from "../services/student.service";
import { UserProfileService } from "../services/user-profile.service";
import { Project } from "../model/project";
import { StudentTeam } from "../model/studentTeam";
import { User } from "../model/user";
import { Student } from "../model/student";
import {Location} from "@angular/common";
import {MatSnackBar} from "@angular/material";

@Component({
  selector: 'app-view-team',
  templateUrl: './edit-team.component.html',
  styleUrls: ['./edit-team.component.css']
})
export class EditTeamComponent implements OnInit {
    project: Project;
    team: StudentTeam;
    fileToUpload: File;
    newFilename = "";
    leaderSuggestions: Observable<User[]>;
    studentSuggestions: Observable<Student[]>;
    userAutocomplete = new FormControl();
    studentAutocomplete = new FormControl();
    leaders: User[];
    students: Student[];

    constructor(
        private appTitleService: AppTitleService,
        private projectService: ProjectService,
        private teamService: TeamService,
        private studentService: StudentService,
        private route: ActivatedRoute,
        private userProfileService: UserProfileService,
        private location: Location,
        private snackBar: MatSnackBar) {
        this.project = new Project();
        this.team = new StudentTeam();
    }

    loadProjectAndTeam() {
        const projectId = +this.route.snapshot.paramMap.get('projectId');
        const teamId = +this.route.snapshot.paramMap.get('teamId');

        if (projectId) {
            this.projectService.getProjectObservable(projectId).subscribe(project => {
                this.project = project;
                this.appTitleService.setTitle(`Edit team for ${project.name}`);
                this.appTitleService.setCurrentProject(project);
            });
        }

        if (teamId) {
            this.teamService.getTeamObservable(teamId).subscribe(team => {
                this.team = team;
                this.leaders = team.leaders;
                this.students = team.students;
            });
        }
    }

    private setFile(event) {
        if (event.srcElement.files && event.srcElement.files.length >= 1) {
            this.fileToUpload = event.srcElement.files[0];
            this.newFilename = this.fileToUpload.name;
        }
    }

    private leaderChosen(event) {
        let found = false;
        for (let i = 0; i < this.leaders.length && !found; i++) {
            if (this.leaders[i].id === this.userAutocomplete.value.id) {
                found = true;
            }
        }
        if (!found) {
            this.leaders.push(this.userAutocomplete.value);
        }
        this.userAutocomplete.setValue(undefined);
    }

    private removeLeader(leaderId: number) {
        let found = false;
        for (let i = 0; i < this.leaders.length && !found; i++) {
            if (this.leaders[i].id === leaderId) {
                found = true;
                this.leaders.splice(i, 1);
            }
        }
    }

    private studentChosen(event) {
        let found = false;
        for (let i = 0; i < this.students.length && !found; i++) {
            if (this.students[i].id === this.studentAutocomplete.value.id) {
                found = true;
            }
        }
        if (!found) {
            this.students.push(this.studentAutocomplete.value);
        }
        this.studentAutocomplete.setValue(undefined);
    }

    private removeStudent(studentId: number) {
        let found = false;
        for (let i = 0; i < this.students.length && !found; i++) {
            if (this.students[i].id === studentId) {
                found = true;
                this.students.splice(i, 1);
            }
        }
    }

    onSubmit() {
        let studentList = "";
        let leaderList = "";
        for (let i=0; i < this.students.length; i++) {
            if (i != 0) {
                studentList += ",";
            }
            studentList += this.students[i].id.toString();
        }
        for (let i=0; i < this.leaders.length; i++) {
            if (i != 0) {
                leaderList += ",";
            }
            leaderList += this.leaders[i].id.toString();
        }

        this.teamService.updateTeamObservable(this.team, studentList, leaderList, this.fileToUpload).subscribe(team => {
            this.team = team;
            this.leaders = team.leaders;
            this.students = team.students;
            this.snackBar.open(`Team updated`, 'Dismiss', {
                duration: 10000,
            });
            this.newFilename = "";
        });
    }

    onCancel() {
        this.location.back();
    }

    ngOnInit() {
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.loadProjectAndTeam());
        // Set up suggestions for leaders
        this.userAutocomplete.valueChanges.subscribe(
            newValue => {
                if (newValue && newValue.length >= 2) {
                    this.leaderSuggestions = this.userProfileService.loadUserMatchingForProject(this.project.id, newValue)
                }
            });
        // Set up suggestions for students
        this.studentAutocomplete.valueChanges.subscribe(
            newValue => {
                if (newValue && newValue.length >= 2) {
                    this.studentSuggestions = this.studentService.loadStudentMatchingForProject(this.project.id, newValue)
                }
            });
    }

}
