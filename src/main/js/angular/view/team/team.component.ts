import { Component, OnInit } from '@angular/core';
import { AppTitleService } from "../../shared/services/app-title.service";
import { ProjectService } from "../../shared/services/project.service";
import { Project } from "../../shared/model/project";
import {ActivatedRoute, Router} from "@angular/router";
import {StudentTeam} from "../../shared/model/studentTeam";
import {TeamService} from "../../shared/services/team.service";

@Component({
  selector: 'view-team',
  templateUrl: './team.component.html',
  styleUrls: ['./team.component.css']
})
export class TeamComponent implements OnInit {
    project: Project;
    team: StudentTeam;
    displayedColumns = ['name', 'warnings'];

    constructor(
        private appTitleService: AppTitleService,
        private route: ActivatedRoute,
        private router: Router,
        private projectService: ProjectService,
        private teamService: TeamService
    ) {
    }

    loadProjectAndStudents() {
        const projectId = +this.route.snapshot.paramMap.get('projectId');
        if (projectId) {
            this.projectService.getProject(projectId).subscribe(project => {
                this.project = project;
                this.appTitleService.setCurrentProject(project);
            });
            const teamId = +this.route.snapshot.paramMap.get('teamId');

            this.teamService.getTeam(teamId).subscribe(team => {
                this.appTitleService.setTitle(team.name);
                this.team = team;
            });
        }
    }

    adjustPoints(points: number) {
        this.teamService.updatePoints(this.team.id, points).subscribe(team => {
            if (team) {
                this.team = team;
            }
        })
    }

    ngOnInit() {
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.loadProjectAndStudents());
    }
}
