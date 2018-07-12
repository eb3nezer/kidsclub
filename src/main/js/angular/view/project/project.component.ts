import { Component, OnInit } from '@angular/core';
import { AppTitleService } from "../../shared/services/app-title.service";
import { ProjectService } from "../../shared/services/project.service";
import { Project } from "../../shared/model/project";
import {ActivatedRoute, Router} from "@angular/router";
import {Location} from "@angular/common";
import {StudentTeam} from "../../shared/model/studentTeam";
import {TeamService} from "../../shared/services/team.service";
import {ConfirmDialogComponent} from "../../shared/confirm-dialog/confirm-dialog.component";
import {MatSnackBar} from "@angular/material";
import {MatDialog} from '@angular/material';
import {UserProfileService} from "../../shared/services/user-profile.service";

@Component({
  selector: 'view-project',
  templateUrl: './project.component.html',
  styleUrls: ['./project.component.css']
})
export class ProjectComponent implements OnInit {
    project: Project;
    teams: StudentTeam[];
    membersDisabled = true;
    studentsDisabled = true;

    constructor(
        private apptitleService: AppTitleService,
        private route: ActivatedRoute,
        private router: Router,
        private location: Location,
        private projectService: ProjectService,
        private teamService: TeamService,
        private dialog: MatDialog,
        private snackBar: MatSnackBar,
        private userProfileService: UserProfileService
    ) {
    }

    loadProjects() {
        const projectId = +this.route.snapshot.paramMap.get('id');
        if (projectId) {
            this.projectService.getProject(projectId).subscribe(project => {
                this.project = project;
                this.apptitleService.setCurrentProject(this.project);
            });
            this.teamService.getTeamsForProject(projectId).subscribe(teams => {
                this.teams = teams;
            });
            this.userProfileService.getMyPermissionsForProject(projectId).subscribe(permissions => {
                if (permissions) {
                    this.membersDisabled = !UserProfileService.checkProjectPermissionGranted(permissions, "LIST_USERS");
                    this.studentsDisabled = !UserProfileService.checkProjectPermissionGranted(permissions, "VIEW_STUDENTS");
                }
            })
        }
    }

    resetPoints() {
        let dialogRef = this.dialog.open(ConfirmDialogComponent, {
            width: '250px',
            data: {
                title: "Confirm",
                text: `Are you sure you wish to reset all teams' points? This operation cannot be undone.`,
                buttons: ["Yes", "Cancel"]
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result === "Yes") {
                this.teamService.resetPoints(this.project.id).subscribe(next => {
                    if (next) {
                        this.teams = next;

                        this.snackBar.open(`All points reset`, 'Dismiss', {
                            duration: 10000,
                        })
                    }
                });
            }
        });
    }

    ngOnInit() {
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.loadProjects());
        Promise.resolve(null).then(() => this.apptitleService.setTitle("Home"));
    }
}
