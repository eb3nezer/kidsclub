import { Component, OnInit } from '@angular/core';
import {MatDialog} from '@angular/material';
import { AppTitleService } from "../services/app-title.service";
import { ProjectService } from "../services/project.service";
import { TeamService } from "../services/team.service";
import { Project } from "../model/project";
import {ActivatedRoute, Router} from "@angular/router";
import {User} from "../model/user";
import {ConfirmDialogComponent} from "../confirm-dialog/confirm-dialog.component";
import {InviteService} from "../services/invite.service";
import {MatSnackBar} from "@angular/material";

@Component({
  selector: 'app-view-members',
  templateUrl: './view-members.component.html',
  styleUrls: ['./view-members.component.css']
})
export class ViewMembersComponent implements OnInit {
    project: Project;
    projectUsers: User[];
    displayedColumns = ['userName', 'userPhone', 'userEmail', 'userPhoto', 'userNotes'];

    constructor(
        private appTitleService: AppTitleService,
        private projectService: ProjectService,
        private teamService: TeamService,
        private route: ActivatedRoute,
        private inviteService: InviteService,
        private dialog: MatDialog,
        private snackBar: MatSnackBar) {
    }

    loadProject() {
        const projectId = +this.route.snapshot.paramMap.get('id');
        if (projectId) {
            this.projectService.getProjectObservable(projectId).subscribe(project => {
                this.project = project;
                this.projectUsers = project.users;
                this.appTitleService.setTitle(`${project.name} Project Members`);
                this.appTitleService.setCurrentProject(project);
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
