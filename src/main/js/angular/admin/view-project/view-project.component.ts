import { Component, OnInit } from '@angular/core';
import { AppTitleService } from "../../shared/services/app-title.service";
import { ProjectService } from "../../shared/services/project.service";
import { TeamService } from "../../shared/services/team.service";
import { Project } from "../../shared/model/project";
import {ActivatedRoute} from "@angular/router";
import { StudentTeam } from "../../shared/model/studentTeam";
import { UserProfileService} from "../../shared/services/user-profile.service";

@Component({
  selector: 'app-view-project',
  templateUrl: './view-project.component.html',
  styleUrls: ['./view-project.component.css']
})
export class ViewProjectComponent implements OnInit {
    project: Project;
    teams: StudentTeam[];
    displayedColumns = ['teamName', 'teamAvatar', 'teamScore', 'teamStudents'];
    editProjectDisabled = true;
    viewStudentsDisabled = true;
    editAlbumsDisabled = true;
    editDocumentsDisabled = true;
    editMembersDisabled = true;

    constructor(
        private appTitleService: AppTitleService,
        private projectService: ProjectService,
        private teamService: TeamService,
        private route: ActivatedRoute,
        private userProfileService: UserProfileService) {
    }

    loadProjectAndTeams() {
        const projectId = +this.route.snapshot.paramMap.get('id');
        if (projectId) {
            this.projectService.getProject(projectId, false).subscribe(project => {
                this.project = project;
                this.appTitleService.setTitle(`${project.name} Project Administration`);
                this.appTitleService.setCurrentProject(project);
            });

            this.teamService.getTeamsForProject(projectId, 'students').subscribe(teams => {
                this.teams = teams;
            });

            this.userProfileService.getMyPermissionsForProject(projectId).subscribe(permissions => {
               if (permissions) {
                   this.editProjectDisabled = !(UserProfileService.checkProjectPermissionGranted(permissions, "PROJECT_ADMIN"));
                   this.viewStudentsDisabled = !(UserProfileService.checkProjectPermissionGranted(permissions, "VIEW_STUDENTS"));
                   this.editAlbumsDisabled = !(UserProfileService.checkProjectPermissionGranted(permissions, "EDIT_ALBUMS"));
                   this.editDocumentsDisabled = !(UserProfileService.checkProjectPermissionGranted(permissions, "EDIT_DOCUMENTS"));
                   this.editMembersDisabled = !(UserProfileService.checkProjectPermissionGranted(permissions, "LIST_USERS"));
               }
            });
        }
    }

    ngOnInit() {
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.loadProjectAndTeams());
    }
}
