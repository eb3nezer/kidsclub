import { Component, OnInit } from '@angular/core';
import { Observable } from "rxjs/index";
import { FormControl } from "@angular/forms";
import { ActivatedRoute } from "@angular/router";
import { AppTitleService } from "../../shared/services/app-title.service";
import { ProjectService } from "../../shared/services/project.service";
import { TeamService } from "../../shared/services/team.service";
import { StudentService } from "../../shared/services/student.service";
import { UserProfileService } from "../../shared/services/user-profile.service";
import { Project } from "../../shared/model/project";
import { StudentTeam } from "../../shared/model/studentTeam";
import { User } from "../../shared/model/user";
import { Student } from "../../shared/model/student";
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
    saveTeamDisabled = true;

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
            this.projectService.getProject(projectId).subscribe(project => {
                this.project = project;
                this.appTitleService.setTitle(`Edit team for ${project.name}`);
                this.appTitleService.setCurrentProject(project);
            });

            this.userProfileService.getMyPermissionsForProject(projectId).subscribe(permissions => {
                if (permissions) {
                    this.saveTeamDisabled = !(UserProfileService.checkProjectPermissionGranted(permissions, "PROJECT_ADMIN"));
                }
            });
        }

        if (teamId) {
            this.teamService.getTeam(teamId).subscribe(team => {
                this.team = team;
                this.leaders = team.leaders;
                this.students = team.students;
            });
        }
    }

    setFile(event) {
        if (event.srcElement.files && event.srcElement.files.length >= 1) {
            this.fileToUpload = event.srcElement.files[0];
            this.newFilename = this.fileToUpload.name;
        }
    }

    leaderChosen(event) {
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

    removeLeader(leaderId: number) {
        let found = false;
        for (let i = 0; i < this.leaders.length && !found; i++) {
            if (this.leaders[i].id === leaderId) {
                found = true;
                this.leaders.splice(i, 1);
            }
        }
    }

    studentChosen(event) {
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

    removeStudent(studentId: number) {
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

        this.teamService.updateTeam(this.team, studentList, leaderList, this.fileToUpload).subscribe(team => {
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
