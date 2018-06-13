import { Component, OnInit } from '@angular/core';
import { AppTitleService } from "../../shared/services/app-title.service";
import { ProjectService } from "../../shared/services/project.service";
import { Project } from "../../shared/model/project";
import {ActivatedRoute, Router} from "@angular/router";
import {Location} from "@angular/common";
import {StudentTeam} from "../../shared/model/studentTeam";
import {TeamService} from "../../shared/services/team.service";

@Component({
  selector: 'view-leader',
  templateUrl: './leader.component.html',
  styleUrls: ['./leader.component.css']
})
export class LeaderComponent implements OnInit {
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
        this.projectService.getProjectObservable(projectId).subscribe(project => {
            this.project = project;
            this.apptitleService.setCurrentProject(this.project);
        });
        this.teamService.getMyTeams(projectId).subscribe(teams => {
            this.teams = teams;
        })
    }

    ngOnInit() {
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.apptitleService.setTitle("Leader View"));
        Promise.resolve(null).then(() => this.loadProjects());
    }
}
