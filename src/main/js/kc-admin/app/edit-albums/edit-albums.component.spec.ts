import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EditAlbumsComponent } from './edit-albums.component';

describe('EditAlbumsComponent', () => {
  let component: EditAlbumsComponent;
  let fixture: ComponentFixture<EditAlbumsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EditAlbumsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditAlbumsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
