import { Component, OnInit } from '@angular/core';
import { AppTitleService } from "../../shared/services/app-title.service";
import { ProjectService } from "../../shared/services/project.service";
import { StudentService } from "../../shared/services/student.service";
import { Project } from "../../shared/model/project";
import {ActivatedRoute} from "@angular/router";
import { Location } from '@angular/common';
import {MatCheckboxChange, MatSnackBar} from "@angular/material";
import {UserProfileService} from "../../shared/services/user-profile.service";
import {UserPermissions} from "../../shared/model/userPermissions";

@Component({
  selector: 'app-user-permissions',
  templateUrl: './user-permissions.component.html',
  styleUrls: ['./user-permissions.component.css']
})
export class UserPermissionsComponent implements OnInit {
    project: Project;
    permissions: UserPermissions;

    constructor(
        private appTitleService: AppTitleService,
        private projectService: ProjectService,
        private studentService: StudentService,
        private userProfileService: UserProfileService,
        private route: ActivatedRoute,
        private location: Location,
        public snackBar: MatSnackBar) {
    }

    ngOnInit() {
    }

    loadData() {
        const projectId = +this.route.snapshot.paramMap.get('projectId');
        if (projectId) {
            this.projectService.getProject(projectId).subscribe(project => {
                this.project = project;
                this.appTitleService.setCurrentProject(project);
            });
        }
        const userId = +this.route.snapshot.paramMap.get('userId');
        if (userId) {
            this.userProfileService.getPermissionsForUserAndProject(projectId, userId).subscribe(user => {
                this.permissions = user;
                this.appTitleService.setTitle(`Edit Permissions for ${this.permissions.user.name}`);
            });
        }
    }

    onToggleProjectPermission(event: MatCheckboxChange) {
        const newValue = event.checked;
        const permission = event.source.name;
        this.userProfileService.setUserProjectPermission(this.permissions.user.id, this.project.id, permission, newValue).subscribe(updated => {
            this.snackBar.open('Updated', undefined, {
                duration: 2000,
            });
        });
    }

    onToggleSitePermission(event: MatCheckboxChange) {
        const newValue = event.checked;
        const permission = event.source.name;
        this.userProfileService.setUserSitePermission(this.permissions.user.id, permission, newValue).subscribe(updated => {
            this.snackBar.open('Updated', undefined, {
                duration: 2000,
            });
        });
    }

    onCancel() {
        this.location.back();
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.loadData());
    }
}
