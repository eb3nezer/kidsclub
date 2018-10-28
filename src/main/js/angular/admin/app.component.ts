import { Component } from '@angular/core';
import { AppTitleService } from "../shared/services/app-title.service";
import {Project} from "../shared/model/project";
import { UserProfileService } from "../shared/services/user-profile.service";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})

export class AppComponent {
    pageTitle: string = "";
    projectId: number;
    currentProject: Project;
    systemAdmin = false;
    membersDisabled = true;
    studentsDisabled = true;
    documentsDisabled = true;
    albumsDisabled = true;
    auditDisabled = true;
    createProjectDisabled = true;

    constructor(
        private apptitleService: AppTitleService,
        private userProfileService: UserProfileService) {
    }

    ngOnInit() {
        // Clear loading mask
        document.getElementById("loadingDiv").style.display = "none";

        this.apptitleService.getTitleObserver().subscribe(title => {
            this.pageTitle = title;
            document.title = title;
        });

        this.apptitleService.getCurrentProjectObserver().subscribe(project => {
            if (!this.currentProject || !(this.currentProject.id == project.id)) {
                this.userProfileService.getMyPermissionsForProject(project.id).subscribe(permissions => {
                    if (permissions) {
                        this.systemAdmin = UserProfileService.checkSitePermissionGranted(permissions, "SYSTEM_ADMIN");
                        this.membersDisabled = !(this.systemAdmin ||
                            UserProfileService.checkProjectPermissionGranted(permissions, "LIST_USERS"));
                        this.studentsDisabled = !(this.systemAdmin ||
                            UserProfileService.checkProjectPermissionGranted(permissions, "VIEW_STUDENTS"));
                        this.documentsDisabled = !UserProfileService.checkProjectPermissionGranted(permissions, "EDIT_DOCUMENTS");
                        this.albumsDisabled = !UserProfileService.checkProjectPermissionGranted(permissions, "EDIT_ALBUMS");
                        this.auditDisabled = !UserProfileService.checkSitePermissionGranted(permissions, "VIEW_AUDIT");
                        this.createProjectDisabled = !UserProfileService.checkSitePermissionGranted(permissions, "CREATE_PROJECT");
                    }
                });
            }
            this.currentProject = project;
        });
    }
}
