import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import { Location } from '@angular/common';
import {MatSnackBar} from "@angular/material";

import { AppTitleService } from "../services/app-title.service";
import {ProjectService} from "../services/project.service";
import {Project} from "../model/project";
import {InviteService} from "../services/invite.service";

@Component({
  selector: 'app-invite-member',
  templateUrl: './invite-member.component.html',
  styleUrls: ['./invite-member.component.css']
})
export class InviteMemberComponent implements OnInit {
    project: Project;
    bulkInvite: boolean;
    email: string;
    name: string;
    bulkEmails: string;

    constructor(
        private appTitleService: AppTitleService,
        private route: ActivatedRoute,
        private router: Router,
        private location: Location,
        private projectService: ProjectService,
        private snackBar: MatSnackBar,
        private invitationService: InviteService) {
    }

    loadProject() {
        const projectId = +this.route.snapshot.paramMap.get('projectId');
        if (projectId) {
            this.projectService.getProjectObservable(projectId).subscribe(project => {
                this.project = project;
                this.appTitleService.setCurrentProject(project);
                const inviteType = +this.route.snapshot.paramMap.get('inviteType');
                // magic
                if (inviteType == 5) {
                    this.bulkInvite = true;
                    this.appTitleService.setTitle(`Bulk invite team members to ${project.name}`);
                } else {
                    this.bulkInvite = false;
                    this.appTitleService.setTitle(`Invite a team member to ${project.name}`);
                }
            });
        }
    }

    onSubmit() {
        if (this.bulkInvite) {
            this.invitationService.inviteUsers(this.project.id, this.bulkEmails).subscribe(users => {
                this.snackBar.open(`Invited ${users.length} users`, 'Dismiss', {
                    duration: 10000,
                });
                this.bulkEmails = undefined;
            });
        } else {
            this.invitationService.inviteUser(this.project.id, this.email, this.name).subscribe(user => {
                if (user) {
                    this.snackBar.open(`Invited ${user.email}`, 'Dismiss', {
                        duration: 10000,
                    });
                    this.email = undefined;
                    this.name = undefined;
                }
            });
        }
    }

    onCancel() {
        this.location.back();
    }

    ngOnInit() {
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.loadProject());
    }

}
