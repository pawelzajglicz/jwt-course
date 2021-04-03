import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { NavigationComponent } from './navigation/navigation.component';
import { UpperPanelComponent } from './upper-panel/upper-panel.component';

@NgModule({
  declarations: [
    AppComponent,
    NavigationComponent,
    UpperPanelComponent
  ],
  imports: [,
    AppRoutingModule
    BrowserModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
