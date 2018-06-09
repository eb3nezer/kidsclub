import { Component, OnInit } from '@angular/core';
import { ApptitleService } from "../apptitle.service";

@Component({
  selector: 'app-admin-home',
  templateUrl: './admin-home.component.html',
  styleUrls: ['./admin-home.component.css']
})
export class AdminHomeComponent implements OnInit {

  constructor(private apptitleService: ApptitleService) {

  }

  ngOnInit() {
  }

  ngAfterViewInit() {
    Promise.resolve(null).then(() => this.apptitleService.setTitle("Administration Home"));
  }
}
