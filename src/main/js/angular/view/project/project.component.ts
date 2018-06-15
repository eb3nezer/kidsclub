import { Component, OnInit } from '@angular/core';
import { AppTitleService } from "../../shared/services/app-title.service";
import { ProjectService } from "../../shared/services/project.service";
import { Project } from "../../shared/model/project";
import {ActivatedRoute, Router} from "@angular/router";
import {Location} from "@angular/common";
import {StudentTeam} from "../../shared/model/studentTeam";
import {TeamService} from "../../shared/services/team.service";

@Component({
  selector: 'view-project',
  templateUrl: './project.component.html',
  styleUrls: ['./project.component.css']
})
export class ProjectComponent implements OnInit {
    project: Project;
    teams: StudentTeam[];

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
        this.projectService.getProject(projectId).subscribe(project => {
            this.project = project;
            this.apptitleService.setCurrentProject(this.project);
        });
        this.teamService.getTeamsForProject(projectId).subscribe(teams => {
            this.teams = teams;
        })
    }

    ngOnInit() {
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.loadProjects());
        Promise.resolve(null).then(() => this.apptitleService.setTitle("Home"));
    }
}
