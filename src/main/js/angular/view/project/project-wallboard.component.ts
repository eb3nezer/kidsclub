import {AfterViewInit, Component, OnDestroy, OnInit} from '@angular/core';
import { AppTitleService } from "../../shared/services/app-title.service";
import { ProjectService } from "../../shared/services/project.service";
import { Project } from "../../shared/model/project";
import {ActivatedRoute} from "@angular/router";
import {StudentTeam} from "../../shared/model/studentTeam";
import {TeamService} from "../../shared/services/team.service";

@Component({
  selector: 'view-wallboard',
  templateUrl: './project-wallboard.component.html',
  styleUrls: ['./project-wallboard.component.css']
})
export class ProjectWallboardComponent implements OnInit, OnDestroy, AfterViewInit {
    project: Project;
    teams: StudentTeam[];
    wallboardRows: StudentTeam[][];
    wallboardColumns: number;
    timer: any;
    hideTransmit = true;
    hideTransmitError = true;

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
        this.projectService.getProject(projectId, false).subscribe(project => {
            this.project = project;
            this.apptitleService.setCurrentProject(this.project);
            if (this.project.properties.wallboardColumns) {
                this.wallboardColumns = parseInt(this.project.properties.wallboardColumns);
            } else {
                this.wallboardColumns = 0;
            }
            this.hideTransmit = false;
            this.teamService.getTeamsForProject(projectId, "none").subscribe(teams => {
                this.hideTransmit = true;
                if (teams) {
                    this.formatTeams(teams);
                    // Refresh every 10 seconds
                    this.timer = setInterval(this.refresh.bind(this), 10000);
                } else {
                    this.hideTransmitError = false;
                }
            })
        });
    }

    refresh() {
        console.log("Refresh");
        this.hideTransmit = false;
        this.hideTransmitError = true;
        this.teamService.getTeamsForProjectLogError(this.project.id, "none").subscribe(teams => {
            this.hideTransmit = true;
            if (teams) {
                this.formatTeams(teams);
            } else {
                this.hideTransmitError = false;
                console.warn("No teams returned. Assuming an error happened. Will retry.")
            }
        })
    }

    ngOnInit() {
    }

    ngOnDestroy() {
        console.log("Remove wallboard timer");
        clearInterval(this. timer);
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.loadProjects());
        Promise.resolve(null).then(() => this.apptitleService.setTitle("Scoreboard"));
    }
}
