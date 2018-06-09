import { Component } from '@angular/core';
import { ApptitleService } from "./apptitle.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent {
  pageTitle = "";

  constructor(private apptitleService: ApptitleService) {

  }

  ngOnInit() {
    this.apptitleService.getTitle().subscribe(title => {
        this.pageTitle = title;
        console.log(`Got new title ${title}`);
        document.title = title;
      },
      e => console.log('on error %s', e),
      () => console.log('on complete'));
  }

  ngAfterViewInit() {
  }
}
