import {Component, Input, OnInit} from '@angular/core';
import { AppTitleService } from "../../shared/services/app-title.service";
import { ProjectService } from "../../shared/services/project.service";
import { Project } from "../../shared/model/project";
import {ActivatedRoute, Router} from "@angular/router";
import {Location} from "@angular/common";
import {StudentTeam} from "../../shared/model/studentTeam";
import {TeamService} from "../../shared/services/team.service";
import {User} from "../../shared/model/user";
import {Student} from "../../shared/model/student";

@Component({
  selector: 'view-leader-team',
  templateUrl: './leader-team.component.html',
  styleUrls: ['./leader.component.css']
})
export class LeaderTeamComponent implements OnInit {
    @Input() team: StudentTeam;
    @Input() project: Project;
    leaders: User[];
    students: Student[];

    constructor(
    ) {
    }

    ngOnInit() {
        this.leaders = this.team.leaders;
        this.students = this.team.students;
    }
}
