import { Component, OnInit } from '@angular/core';
import { AppTitleService } from "../../shared/services/app-title.service";
import { ProjectService } from "../../shared/services/project.service";
import { Project } from "../../shared/model/project";
import {ActivatedRoute, Router} from "@angular/router";
import {Location} from "@angular/common";
import {UserProfileService} from '../../shared/services/user-profile.service';

@Component({
  selector: 'app-admin-home',
  templateUrl: './admin-home.component.html',
  styleUrls: ['./admin-home.component.css']
})
export class AdminHomeComponent implements OnInit {
    project: Project;
    projects: Project[];

    constructor(
        private apptitleService: AppTitleService,
        private route: ActivatedRoute,
        private router: Router,
        private location: Location,
        private projectService: ProjectService,
        private userProfileService: UserProfileService
    ) {
    }

    loadProjects() {
        var projectId = +this.route.snapshot.paramMap.get('id');
        if (!projectId) {
            this.projectService.getAllProjects(true).subscribe(projects => {
                if (!projects || projects.length <= 0) {
                    this.userProfileService.getMyPermissions().subscribe(permissions => {
                        if (permissions) {
                            this.apptitleService.setUserPermissions(permissions);
                        }
                    });
                } else if (projects.length > 1) {
                    this.projects = projects;
                } else {
                    // You are a member of only 1 project, so redirect there
                    projectId = projects[0].id;
                    this.router.navigate([`/viewproject/${projectId}`]);
                }
            });
        } else {
            this.projectService.getProject(projectId).subscribe(project => {
                this.project = project
            });
        }
    }

    ngOnInit() {
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.apptitleService.setTitle("Administration Home"));
        Promise.resolve(null).then(() => this.loadProjects());
    }
}
