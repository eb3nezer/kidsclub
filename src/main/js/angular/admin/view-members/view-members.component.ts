import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MatDialog} from "@angular/material/dialog";

import { AppTitleService } from "../../shared/services/app-title.service";
import { ProjectService } from "../../shared/services/project.service";
import { TeamService } from "../../shared/services/team.service";
import { Project } from "../../shared/model/project";
import {User} from "../../shared/model/user";
import {InviteService} from "../../shared/services/invite.service";
import {ConfirmDialogComponent} from "../../shared/confirm-dialog/confirm-dialog.component";
import {UserProfileService} from "../../shared/services/user-profile.service";

@Component({
  selector: 'app-view-members',
  templateUrl: './view-members.component.html',
  styleUrls: ['./view-members.component.css']
})
export class ViewMembersComponent implements OnInit {
    project: Project;
    projectUsers: User[];
    displayedColumns = ['userName', 'userPhone', 'userEmail', 'userPhoto', 'userNotes'];
    inviteDisabled = true;

    constructor(
        private appTitleService: AppTitleService,
        private projectService: ProjectService,
        private teamService: TeamService,
        private route: ActivatedRoute,
        private inviteService: InviteService,
        private dialog: MatDialog,
        private snackBar: MatSnackBar,
        private userProfileService: UserProfileService) {
    }

    loadProject() {
        const projectId = +this.route.snapshot.paramMap.get('id');
        if (projectId) {
            this.projectService.getProject(projectId).subscribe(project => {
                this.project = project;
                this.projectUsers = project.users;
                this.appTitleService.setTitle(`${project.name} Project Members`);
                this.appTitleService.setCurrentProject(project);
            });

            this.userProfileService.getMyPermissionsForProject(projectId).subscribe(permissions => {
                if (permissions) {
                    this.inviteDisabled = !(UserProfileService.checkSitePermissionGranted(permissions, "INVITE_USERS"));
                }
            });
        }
    }

    removeMember(user: User) {
        let dialogRef = this.dialog.open(ConfirmDialogComponent, {
            width: '250px',
            data: {
                title: "Confirm",
                text: `Are you sure you wish to remove ${user.name} from the project? This operation cannot be undone.`,
                buttons: ["Delete", "Cancel"]
            }
        });

        const currentProject = this.project;

        dialogRef.afterClosed().subscribe(result => {
            if (result === "Delete") {
                this.inviteService.unInviteUser(this.project.id, user.id).subscribe(() => {
                    let found = false;
                    for (let i = 0; i < this.project.users.length && !found; i++) {
                        if (this.project.users[i].id == user.id) {
                            found = true;
                            this.project.users.splice(i, 1);
                        }
                    }
                    if (found) {
                        // Makes the table refresh
                        this.projectUsers = this.project.users.slice();
                    }
                    this.snackBar.open(`Team member ${user.name} removed`, 'Dismiss', {
                        duration: 10000,
                    })
                });
            }
        });
    }

    ngOnInit() {
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.loadProject());
    }

}
