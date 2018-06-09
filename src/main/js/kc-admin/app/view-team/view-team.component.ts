import { Component, OnInit } from '@angular/core';
import { AppTitleService } from "../services/app-title.service";
import { ProjectService } from "../services/project.service";
import { TeamService } from "../services/team.service";
import { Project } from "../model/project";
import {ActivatedRoute, Router} from "@angular/router";
import { StudentTeam } from "../model/studentTeam";

@Component({
  selector: 'app-view-team',
  templateUrl: './view-team.component.html',
  styleUrls: ['./view-team.component.css']
})
export class ViewTeamComponent implements OnInit {
    project: Project;
    team: StudentTeam;

  constructor(private appTitleService: AppTitleService,
              private projectService: ProjectService,
              private teamService: TeamService,
              private route: ActivatedRoute) {
      this.project = new Project();
      this.team = new StudentTeam();
  }

    loadProjectAndTeam() {
        const projectId = +this.route.snapshot.paramMap.get('projectId');
        const teamId = +this.route.snapshot.paramMap.get('teamId');

        if (projectId) {
            this.projectService.getProjectObservable(projectId).subscribe(project => {
                this.project = project;
                this.appTitleService.setTitle(`Edit team for ${project.name}`)
            });
        }

        if (teamId) {
            this.teamService.getTeamObservable(teamId).subscribe(team => this.team = team);
        }
    }

    ngOnInit() {
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.loadProjectAndTeam());
    }

}
