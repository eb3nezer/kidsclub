import { Component } from '@angular/core';
import {AppTitleService} from "../shared/services/app-title.service";
import {Project} from "../shared/model/project";
import {UserProfileService} from "../shared/services/user-profile.service";

@Component({
  selector: 'view-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
    pageTitle = "";
    currentProject: Project;
    showToolbar = true;
    attendanceDisabled = true;
    membersDisabled = true;
    studentsDisabled = true;

    constructor(
        private appTitleService: AppTitleService,
        private userProfileService: UserProfileService) {
    }

    ngOnInit() {
        // Clear loading mask
        document.getElementById("loadingDiv").style.display = "none";

        this.appTitleService.getTitleObserver().subscribe(title => {
                this.pageTitle = title;
                document.title = title;
                // magic
                if (title === "Scoreboard") {
                    this.showToolbar = false;
                } else {
                    this.showToolbar = true;
                }
            },
            e => console.log('on error %s', e),
            () => console.log('on complete'));

        this.appTitleService.getCurrentProjectObserver().subscribe(project => {
            if (!this.currentProject || !(this.currentProject.id == project.id)) {
                this.userProfileService.getMyPermissionsForProject(project.id).subscribe(permissions => {
                    if (permissions) {
                        this.attendanceDisabled = !UserProfileService.checkProjectPermissionGranted(permissions, "EDIT_ATTENDANCE");
                        this.membersDisabled = !UserProfileService.checkProjectPermissionGranted(permissions, "LIST_USERS");
                        this.studentsDisabled = !UserProfileService.checkProjectPermissionGranted(permissions, "VIEW_STUDENTS");
                    }
                });
            }
            this.currentProject = project;
        });
    }
}
