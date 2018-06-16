import { Component, OnInit } from '@angular/core';
import { AppTitleService } from "../../shared/services/app-title.service";
import { ProjectService } from "../../shared/services/project.service";
import { Project } from "../../shared/model/project";
import {ActivatedRoute, Router} from "@angular/router";
import {Location} from "@angular/common";
import {TeamService} from "../../shared/services/team.service";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
    project: Project;
    projects: Project[];

    constructor(
        private apptitleService: AppTitleService,
        private route: ActivatedRoute,
        private router: Router,
        private location: Location,
        private projectService: ProjectService,
        private teamService: TeamService
    ) {
    }

    loadProjects() {
        var projectId = +this.route.snapshot.paramMap.get('id');
        if (!projectId) {
            this.projectService.getAllProjects().subscribe(projects => {
                if (projects.length > 1) {
                    this.projects = projects;
                } else {
                    // You are a member of only 1 project, so redirect there
                    projectId = projects[0].id;
                    this.router.navigate([`/home/${projectId}`]);
                    this.project = projects[0];
                    this.apptitleService.setCurrentProject(this.project);
                }
            });
        } else {
            this.projectService.getProject(projectId).subscribe(project => {
                this.project = project;
                this.apptitleService.setCurrentProject(this.project);
                this.teamService.getMyTeams(projectId).subscribe(teams => {
                    if (teams.length > 0) {
                        this.router.navigate([`/leader/${projectId}`]);
                    } else {
                        this.router.navigate([`/project/${projectId}`]);
                    }
                });
            });
        }
    }

    ngOnInit() {
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.apptitleService.setTitle("Home"));
        Promise.resolve(null).then(() => this.loadProjects());
    }
}
