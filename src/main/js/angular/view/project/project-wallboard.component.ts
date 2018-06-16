import {Component, OnDestroy, OnInit} from '@angular/core';
import { AppTitleService } from "../../shared/services/app-title.service";
import { ProjectService } from "../../shared/services/project.service";
import { Project } from "../../shared/model/project";
import {ActivatedRoute} from "@angular/router";
import {StudentTeam} from "../../shared/model/studentTeam";
import {TeamService} from "../../shared/services/team.service";
import {bind} from "@angular/core/src/render3/instructions";

@Component({
  selector: 'view-wallboard',
  templateUrl: './project-wallboard.component.html',
  styleUrls: ['./project-wallboard.component.css']
})
export class ProjectWallboardComponent implements OnInit, OnDestroy {
    project: Project;
    teams: StudentTeam[];
    wallboardRows: StudentTeam[][];
    wallboardColumns: number;
    timer: any;

    constructor(
        private apptitleService: AppTitleService,
        private route: ActivatedRoute,
        private projectService: ProjectService,
        private teamService: TeamService
    ) {
    }

    formatTeams(teams: StudentTeam[]) {
        this.wallboardRows = [[]];
        if (this.wallboardColumns == 0) {
            this.wallboardRows.push(teams);
        } else {
            let index = 0;
            for (let row = 0; row <= Math.floor(teams.length / this.wallboardColumns); row++) {
                const thisRow: StudentTeam[] = [];
                for (let column = 0; column < this.wallboardColumns; column++) {
                    if (index < teams.length) {
                        thisRow.push(teams[index]);
                        index++;
                    }
                }
                this.wallboardRows.push(thisRow);
            }
        }
    }

    loadProjects() {
        var projectId = +this.route.snapshot.paramMap.get('id');
        this.projectService.getProject(projectId).subscribe(project => {
            this.project = project;
            this.apptitleService.setCurrentProject(this.project);
            if (this.project.properties.wallboardColumns) {
                this.wallboardColumns = parseInt(this.project.properties.wallboardColumns);
            } else {
                this.wallboardColumns = 0;
            }
            this.teamService.getTeamsForProject(projectId).subscribe(teams => {
                this.formatTeams(teams);
                this.timer = setInterval(this.refresh.bind(this), 10000);
            })
        });
    }

    refresh() {
        console.log("Refresh");
        this.teamService.getTeamsForProject(this.project.id).subscribe(teams => {
            this.formatTeams(teams);
        })
    }

    ngOnInit() {
    }

    ngOnDestroy() {
        clearInterval(this. timer);
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.loadProjects());
        Promise.resolve(null).then(() => this.apptitleService.setTitle("Scoreboard"));

    }
}
