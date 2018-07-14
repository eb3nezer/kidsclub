import {AfterViewInit, Component, OnDestroy, OnInit} from '@angular/core';
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
export class TeamComponent implements OnInit, OnDestroy, AfterViewInit {
    project: Project;
    team: StudentTeam;
    displayedColumns = ['name', 'warnings', 'attendance'];
    buttonsDisabled = false;
    hideTransmit = true;
    hideTransmitError = true;
    timer: any;
    teamId: number;

    constructor(
        private appTitleService: AppTitleService,
        private route: ActivatedRoute,
        private router: Router,
        private projectService: ProjectService,
        private teamService: TeamService
    ) {
    }

    loadProjectAndStudents() {
        // The route will have the project ID, but we don't need it
        this.teamId = +this.route.snapshot.paramMap.get('teamId');
        if (this.teamId) {
            this.hideTransmit = false;
            this.hideTransmitError = true;
            this.teamService.getTeam(this.teamId).subscribe(team => {
                this.hideTransmit = true;
                if (team) {
                    this.project = team.project;
                    this.appTitleService.setCurrentProject(team.project);
                    this.appTitleService.setTitle(team.name);
                    this.team = team;
                    // Refresh every 5 mins
                    this.timer = setInterval(this.refresh.bind(this), 300000);
                } else {
                    this.hideTransmitError = false;
                }
            });
        } else {
            console.error("No team ID found in URL");
        }
    }

    refresh() {
        console.log("Refresh team page");
        this.hideTransmitError = true;
        this.hideTransmit = false;
        this.teamService.getTeamLogError(this.teamId).subscribe(team => {
            this.hideTransmit = true;
            if (team) {
                this.appTitleService.setTitle(team.name);
                this.team = team;
            } else {
                this.hideTransmitError = false;
                console.warn("No team returned. Assuming an error happened. Will retry.")
            }
        });
    }

    adjustPoints(points: number) {
        this.buttonsDisabled = true;
        this.teamService.updatePoints(this.team.id, points).subscribe(team => {
            if (team) {
                this.team = team;
            }
            this.buttonsDisabled = false;
        })
    }

    ngOnInit() {
    }

    ngOnDestroy() {
        console.log("Remove team page timer");
        clearInterval(this. timer);
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.loadProjectAndStudents());
    }
}
