import { Component, OnInit } from '@angular/core';
import { AppTitleService } from "../services/app-title.service";
import { ProjectService } from "../services/project.service";
import { TeamService } from "../services/team.service";
import { Project } from "../model/project";
import {ActivatedRoute} from "@angular/router";
import { StudentTeam } from "../model/studentTeam";

@Component({
  selector: 'app-view-project',
  templateUrl: './view-project.component.html',
  styleUrls: ['./view-project.component.css']
})
export class ViewProjectComponent implements OnInit {
    project: Project;
    teams: StudentTeam[];
    displayedColumns = ['teamName', 'teamAvatar', 'teamScore', 'teamStudents'];

    constructor(
        private appTitleService: AppTitleService,
        private projectService: ProjectService,
        private teamService: TeamService,
        private route: ActivatedRoute) {
    }

    loadProjectAndTeams() {
        const projectId = +this.route.snapshot.paramMap.get('id');
        if (projectId) {
            this.projectService.getProjectObservable(projectId).subscribe(project => {
                this.project = project;
                this.appTitleService.setTitle(`${project.name} Project Administration`)
                this.appTitleService.setCurrentProject(project);
            });

            this.teamService.getTeamsForProject(projectId).subscribe(teams => {
                this.teams = teams;
            })
        }
    }

    ngOnInit() {
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.loadProjectAndTeams());
    }
}
